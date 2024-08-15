package com.asemicanalytics.config.mapper;

public record FullColumnId(
    String logicalTableId,
    String columnId
) {
  public static FullColumnId parse(String fullColumnId) {
    var tokens = fullColumnId.split("\\.", 2);
    if (tokens.length != 2) {
      throw new IllegalArgumentException("Invalid full column id format: " + fullColumnId);
    }
    return new FullColumnId(tokens[0], tokens[1]);
  }
}
