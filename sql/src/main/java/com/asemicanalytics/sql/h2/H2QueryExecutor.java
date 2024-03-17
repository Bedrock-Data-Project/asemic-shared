package com.asemicanalytics.sql.h2;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.sql.sql.executor.JdbcQueryExecutor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class H2QueryExecutor extends JdbcQueryExecutor {

  private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendOffset("+HH", "+00")
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

  protected DateTimeFormatter getFormatter() {
    return formatter;
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
      case "STRING", "TEXT", "CHAR", "VARCHAR" -> DataType.STRING;
      case "BOOLEAN" -> DataType.BOOLEAN;
      default -> throw new RuntimeException("Unsupported column type: " + columnType);
    };
  }
}
