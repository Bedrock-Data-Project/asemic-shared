package com.asemicanalytics.sql.sql.snowflake;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.sql.sql.executor.JdbcQueryExecutor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class SnowflakeQueryExecutor extends JdbcQueryExecutor {

  private final String user;
  private final String password;
  private final String jdbcUrl;

  public SnowflakeQueryExecutor(String user, String password,
                                String jdbcUrl, int maxWorkers) {
    super(maxWorkers, new SnowflakeDialect());
    this.user = user;
    this.password = password;
    this.jdbcUrl = jdbcUrl;
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
