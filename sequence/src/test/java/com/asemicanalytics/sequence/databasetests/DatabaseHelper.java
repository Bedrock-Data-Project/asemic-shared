package com.asemicanalytics.sequence.databasetests;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.h2.H2Dialect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
  public static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  public static final String USER = "sa";
  public static final String PASSWORD = "";

  public static void createUserActionTable(TableReference tableReference, String selectSql)
      throws SQLException {

    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(
          new H2Dialect().createTableFromSelect(selectSql, tableReference, true));
    }
  }

  public static void dropAllTables() throws SQLException {
    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate("DROP ALL OBJECTS");
    }
  }
}
