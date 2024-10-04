package com.asemicanalytics.sql.sql.h2;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.sql.sql.executor.JdbcQueryExecutor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;


public class H2QueryExecutor extends JdbcQueryExecutor {

  private static final DateTimeFormatter datetimeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .toFormatter();

  private final String user;
  private final String password;
  private final String jdbcUrl;

  public H2QueryExecutor(String user, String password,
                         String jdbcUrl, int maxWorkers) {
    super(maxWorkers, new H2Dialect());
    this.user = user;
    this.password = password;
    this.jdbcUrl = jdbcUrl;
    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public DateTimeFormatter getDatetimeFormatter() {
    return datetimeFormatter;
  }

  @Override
  protected Connection getConnection() throws InterruptedException {
    try {
      return DriverManager.getConnection(jdbcUrl, user, password);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected DataType getDataType(String columnType) {
    return switch (columnType) {
      case "DATE" -> DataType.DATE;
      case "TIMESTAMP", "TIMESTAMPTZ" -> DataType.DATETIME;
      case "FLOAT", "REAL", "DOUBLE" -> DataType.NUMBER;
      case "INTEGER", "SMALLINT", "BIGINT", "DECIMAL" -> DataType.INTEGER;
      case "STRING", "TEXT", "CHAR", "VARCHAR", "CHARACTER VARYING" -> DataType.STRING;
      case "BOOLEAN" -> DataType.BOOLEAN;
      default -> throw new RuntimeException("Unsupported column type: " + columnType);
    };
  }

  @Override
  public void executeDdl(String sql) {
    System.out.println("Executing DDL: " + sql);
    super.executeDdl(sql);
  }

  @Override
  protected SqlResult executeQuery(String sql, List<DataType> dataTypes, boolean dryRun)
      throws InterruptedException {
    System.out.println("Executing query: " + sql);
    return super.executeQuery(sql, dataTypes, dryRun);
  }

  @Override
  protected List<String> getTables(String schema) throws InterruptedException {
    throw new UnsupportedOperationException("Not implemented");
  }
}
