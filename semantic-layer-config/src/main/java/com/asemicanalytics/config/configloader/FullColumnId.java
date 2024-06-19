package com.asemicanalytics.config.configloader;

public record FullColumnId(
    String datasourceId,
    String columnId
) {
  public static FullColumnId parse(String fullColumnId) {
    var tokens = fullColumnId.split("\\.");
    if (tokens.length != 2) {
      throw new IllegalArgumentException("Invalid full column id format: " + fullColumnId);
    }
    return new FullColumnId(tokens[0], tokens[1]);
  }
}
