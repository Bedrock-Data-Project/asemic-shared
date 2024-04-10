package com.asemicanalytics.sql.sql.executor;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.SqlResultRow;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class JdbcQueryExecutor extends ThreadPoolSqlQueryExecutor {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
  Logger logger = LoggerFactory.getLogger(JdbcQueryExecutor.class);

  protected JdbcQueryExecutor(int maxWorkers, Dialect dialect) {
    super(maxWorkers, dialect);
  }

  protected abstract Connection getConnection() throws InterruptedException;

  protected abstract DataType getDataType(String columnType);

  private Object parseObject(ResultSet resultSet, DataType xaxisType, int columnIndex)
      throws SQLException {
    return switch (xaxisType) {
      case DATE -> LocalDate.parse(resultSet.getString(columnIndex))
          .atStartOfDay(ZoneId.of("UTC"));
      case DATETIME -> LocalDateTime.parse(resultSet.getString(columnIndex), getDatetimeFormatter())
          .atZone(ZoneId.of("UTC"));
      case NUMBER -> resultSet.getDouble(columnIndex);
      case INTEGER -> resultSet.getLong(columnIndex);
      case STRING -> resultSet.getString(columnIndex);
      case BOOLEAN -> resultSet.getBoolean(columnIndex);
    };
  }

  protected DateTimeFormatter getDatetimeFormatter() {
    return formatter;
  }

  @Override
  protected SqlResult executeQuery(String sql, List<DataType> dataTypes, boolean dryRun)
      throws InterruptedException {
    logger.info(
        "Executing query for dialect " + getDialect().getClass().getSimpleName() + ": " + sql);
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery(sql)) {
          int columnCount = resultSet.getMetaData().getColumnCount();
          int rowCount = 0;
          List<SqlResultRow> rows = new ArrayList<>();
          while (resultSet.next()) {
            rowCount++;
            List<Object> columns = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
              columns.add(parseObject(resultSet, dataTypes.get(i), i + 1));
            }
            rows.add(new SqlResultRow(columns));
          }

          logger.info("Got {} rows from query", rowCount);
          return new SqlResult(rows);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected List<Column> getColumns(TableReference table) throws InterruptedException {
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery(
            "SELECT * FROM " + getDialect().tableIdentifier(table) + " WHERE FALSE")) {
          List<Column> columns = new ArrayList<>();

          ResultSetMetaData metaData = resultSet.getMetaData();
          int columnCount = metaData.getColumnCount();

          // Iterate through columns to retrieve column names and types
          for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            String columnType = metaData.getColumnTypeName(i);

            columns.add(
                new Column(columnName, getDataType(columnType), columnName,
                    Optional.empty(), false,
                    false));
          }

          return columns;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CompletableFuture<DatetimeInterval> submitTableFreshness(TableReference table, String id) {
    // TODO we need a better/standard way here when there are no partitions.
    // TODO Probably need to supply date column
    return CompletableFuture.supplyAsync(
        () -> new DatetimeInterval(
            ZonedDateTime.now().minusDays(100000),
            ZonedDateTime.now()
        ));
  }

  @Override
  public void executeDdl(String sql) {
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute(sql);
      }
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
