package com.asemicanalytics.sequence.utils;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.h2.H2Dialect;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
  public static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  public static final String USER = "sa";
  public static final String PASSWORD = "";
  public static final ZonedDateTime BASE_DATE =
      ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

  public static void createUserActionTable(TableReference tableReference, List<UserActionRow> rows)
      throws SQLException {

    List<String> renderedRows = new ArrayList<>();
    for (int i = 0; i < rows.size(); i++) {
      renderedRows.add(rows.get(i).toSelectSql(i == 0));
    }
    String selectSql = String.join(" UNION ALL ", renderedRows);

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
