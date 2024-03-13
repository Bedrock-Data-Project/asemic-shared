package com.asemicanalytics.sequence.databasetests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
  public static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  public static final String USER = "sa";
  public static final String PASSWORD = "";

  private static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS test_table (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))";

  public static void createTable() throws SQLException {

    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    System.out.println("Connected to H2 database.");

    // Create table
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(CREATE_TABLE_SQL);
      System.out.println("Table created successfully.");
    }

    // Run a JDBC query against the table
    try (Statement statement = connection.createStatement()) {
      var resultSet = statement.executeQuery("SELECT * FROM test_table");
      while (resultSet.next()) {
        System.out.println(
            "ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
      }
    }

    connection.close();

  }


}
