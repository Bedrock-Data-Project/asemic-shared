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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;


public abstract class JdbcQueryExecutor extends ThreadPoolSqlQueryExecutor {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  protected JdbcQueryExecutor(int maxWorkers, Dialect dialect) {
    super(maxWorkers, dialect);
  }

  protected abstract Connection getConnection() throws InterruptedException;

  protected abstract DataType getDataType(String columnType);

  private Object parseObject(ResultSet resultSet, DataType xaxisType, int columnIndex)
      throws SQLException {
    if (resultSet.getObject(columnIndex) == null) {
      return null;
    }

    return switch (xaxisType) {
      case DATE -> LocalDate.parse(resultSet.getString(columnIndex));
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
    if (dryRun) {
      return new SqlResult(List.of(), sql, Duration.ZERO, null, 0L);
    }
    var start = Instant.now();
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery(sql)) {
          int columnCount = resultSet.getMetaData().getColumnCount();
          List<SqlResultRow> rows = new ArrayList<>();
          while (resultSet.next()) {
            List<Object> columns = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
              columns.add(parseObject(resultSet, dataTypes.get(i), i + 1));
            }
            rows.add(new SqlResultRow(columns));
          }

          return new SqlResult(rows, sql, Duration.between(start, Instant.now()),
              null, 0L);
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
                    false, Set.of()));
          }

          return columns;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected List<String> getTables(String schema) throws InterruptedException {
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        try (ResultSet resultSet = statement.executeQuery(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + schema
                + "'")) {
          List<String> tables = new ArrayList<>();
          while (resultSet.next()) {
            tables.add(resultSet.getString(1));
          }
          return tables;
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
  public SqlResult executeDdl(String sql) {
    var start = Instant.now();
    try (Connection connection = getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute(sql);
      }
      return new SqlResult(List.of(), sql, Duration.between(start, Instant.now()),
          null, 0L);
    } catch (SQLException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
