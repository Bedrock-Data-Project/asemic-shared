package com.asemicanalytics.core;

import java.util.Optional;

public record TableReference(Optional<String> schemaName, String tableName) {
  public static TableReference of(String schemaName, String tableName) {
    return new TableReference(Optional.of(schemaName), tableName);
  }

  public static TableReference of(String tableName) {
    return new TableReference(Optional.empty(), tableName);
  }

  public static TableReference parse(String table) {
    var tokens = table.split("\\.");
    return switch (tokens.length) {
      case 1 -> TableReference.of(tokens[0]);
      case 2 -> TableReference.of(tokens[0], tokens[1]);
      default -> throw new IllegalStateException("Cant parse table " + table);
    };
  }
}
