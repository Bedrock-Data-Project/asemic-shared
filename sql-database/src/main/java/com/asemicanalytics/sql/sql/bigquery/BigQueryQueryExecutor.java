package com.asemicanalytics.sql.sql.bigquery;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.SqlResultRow;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.dataframe.Dataframe;
import com.asemicanalytics.sql.sql.executor.ThreadPoolSqlQueryExecutor;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.JobStatistics;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BigQueryQueryExecutor extends ThreadPoolSqlQueryExecutor {

  private final BigQuery bigQuery;
  Logger logger = LoggerFactory.getLogger(BigQueryQueryExecutor.class);

  public BigQueryQueryExecutor(String gcpProjectId, String serviceAccountKey, int maxWorkers) {
    super(maxWorkers, new BigQueryDialect());
    InputStream keyStream = new ByteArrayInputStream(serviceAccountKey.getBytes());
    try {
      GoogleCredentials credentials = ServiceAccountCredentials.fromStream(keyStream);

      bigQuery = BigQueryOptions.newBuilder()
          .setProjectId(gcpProjectId)
          .setCredentials(credentials)
          .build()
          .getService();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Object parseFieldValue(FieldValue value, DataType xaxisType) {
    if (value.isNull()) {
      return null;
    }

    return switch (xaxisType) {
      case DATE -> LocalDate.parse(value.getStringValue());
      case DATETIME -> value.getTimestampInstant().atZone(ZoneId.of("UTC"));
      case NUMBER -> value.getDoubleValue();
      case INTEGER -> value.getLongValue();
      case STRING -> value.getStringValue();
      case BOOLEAN -> value.getBooleanValue();
    };
  }

  private List<Object> parseRow(FieldValueList row, List<DataType> dataTypes) {
    List<Object> parsedRow = new LinkedList<>();
    for (int i = 0; i < row.size(); i++) {
      var parsedValue = parseFieldValue(row.get(i), dataTypes.get(i));
      parsedRow.add(parsedValue);
    }
    return parsedRow;
  }

  private ResultWithStatistics executeQuery(String sql, boolean dryRun)
      throws InterruptedException {
    if (dryRun) {
      logger.info("Running BigQuery dry run: {}", sql);
    } else {
      logger.info("Running BigQuery query: {}", sql);
    }
    QueryJobConfiguration queryConfig = QueryJobConfiguration
        .newBuilder(sql)
        .setUseLegacySql(false)
        .setDryRun(dryRun)
        .build();

    JobId jobId = JobId.of(UUID.randomUUID().toString());
    Job queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

    if (dryRun) {
      return new ResultWithStatistics(null, queryJob.getStatistics());
    }

    if (queryJob == null) {
      throw new RuntimeException("Job no longer exists");
    } else if (queryJob.getStatus().getError() != null) {
      throw new RuntimeException(queryJob.getStatus().getError().toString());
    }

    return new ResultWithStatistics(queryJob.getQueryResults(), queryJob.getStatistics());

  }

  @Override
  protected SqlResult executeQuery(String sql, List<DataType> dataTypes, boolean dryRun)
      throws InterruptedException {
    Instant start = Instant.now();
    ResultWithStatistics result = executeQuery(sql, dryRun);
    long bytesProcessed = 0;

    if (dryRun) {
      if (result.statistics.getTotalBytesProcessed() != null) {
        bytesProcessed = result.statistics.getTotalBytesProcessed();
      }
      return new SqlResult(new Dataframe(), sql, Duration.between(start, Instant.now()),
          null, bytesProcessed);
    }

    List<SqlResultRow> rows = new LinkedList<>();
    for (FieldValueList row : result.result().iterateAll()) {
      rows.add(new SqlResultRow(parseRow(row, dataTypes)));
    }

    Schema schema = result.result().getSchema();
    List<String> columnNames = schema.getFields().stream()
        .map(Field::getName)
        .collect(Collectors.toList());
    Dataframe dataframe = Dataframe.fromSqlResult(rows, columnNames, dataTypes);

    Boolean cached = null;
    if (result.statistics() instanceof JobStatistics.QueryStatistics statistics) {
      if (statistics.getTotalBytesProcessed() != null) {
        bytesProcessed = statistics.getTotalBytesProcessed();
      }
      cached = statistics.getCacheHit();
    }
    return new SqlResult(dataframe, sql, Duration.between(start, Instant.now()),
        cached, bytesProcessed);
  }

  private List<Column> getColumnsFromField(String prefix, Field field) {
    List<Column> columns = new ArrayList<>();
    if (field.getType().getStandardType() == StandardSQLTypeName.STRUCT) {
      for (Field subField : field.getSubFields()) {
        columns.addAll(getColumnsFromField(prefix + field.getName() + ".", subField));
      }
    } else {
      columns.add(new Column(prefix + field.getName(),
          switch (field.getType().getStandardType()) {
            case BOOL -> DataType.BOOLEAN;
            case INT64, BIGNUMERIC, NUMERIC -> DataType.INTEGER;
            case FLOAT64 -> DataType.NUMBER;
            case STRING -> DataType.STRING;
            case BYTES, ARRAY, GEOGRAPHY, JSON, INTERVAL, TIME, STRUCT -> null;
            case TIMESTAMP, DATETIME -> DataType.DATETIME;
            case DATE -> DataType.DATE;
          }, prefix + field.getName(), Optional.empty(), true, false, Set.of()));
    }
    return columns;
  }

  @Override
  protected List<Column> getColumns(TableReference table) throws InterruptedException {
    TableId tableIdObject = TableId.of(table.schemaName().get(), table.tableName());
    Schema schema = bigQuery.getTable(tableIdObject).getDefinition().getSchema();
    List<Column> columns = new ArrayList<>();
    for (Field field : schema.getFields()) {
      columns.addAll(getColumnsFromField("", field));
    }
    return columns.stream()
        .filter(column -> column.getDataType() != null)
        .collect(Collectors.toList());
  }

  private ZonedDateTime extractPartitionId(FieldValue fieldValue) {
    if (fieldValue.getValue() == null) {
      return null;
    }

    return LocalDate
        .parse(fieldValue.getStringValue(), DateTimeFormatter.ofPattern("yyyyMMdd"))
        .atStartOfDay(ZoneId.of("UTC"));
  }

  @Override
  public SqlResult executeDdl(String sql) throws InterruptedException {
    return executeQuery(sql, List.of(), false);
  }

  @Override
  public List<String> getTables(String schema) {
    var datasetId = DatasetId.of(schema);
    var tables = new ArrayList<String>();
    for (var table : bigQuery.listTables(datasetId).iterateAll()) {
      tables.add(table.getTableId().getTable());
    }
    return tables;
  }

  @Override
  public CompletableFuture<DatetimeInterval> submitTableFreshness(TableReference table, String id) {
    var future = submit((() -> executeQuery(String.format("""
            SELECT
              MIN(partition_id) AS min,
              MAX(partition_id) AS max
            FROM `%s.INFORMATION_SCHEMA.PARTITIONS`
            WHERE table_name = '%s' AND partition_id != '__NULL__'
            """, table.schemaName().get(), table.tableName()), false)),
        "SqlQueryExecutor.submitTableFreshness");

    return future.thenApply(tableResult -> {
      var row = tableResult.result().iterateAll().iterator().next();
      var min = extractPartitionId(row.get("min"));
      var max = extractPartitionId(row.get("max"));

      if (min != null && max != null) {
        return new DatetimeInterval(min, max);
      }
      return new DatetimeInterval(
          LocalDate.MIN.atStartOfDay(ZoneId.of("UTC")),
          LocalDate.MIN.atStartOfDay(ZoneId.of("UTC")));
    });
  }

  private record ResultWithStatistics(TableResult result,
                                      JobStatistics.QueryStatistics statistics) {
  }
}
