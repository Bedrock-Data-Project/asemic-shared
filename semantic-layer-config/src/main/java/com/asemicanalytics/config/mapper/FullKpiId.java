package com.asemicanalytics.config.mapper;

public record FullKpiId(
    String logicalTableId,
    String kpiId
) {
  public static FullKpiId parse(String fullKpiId) {
    var tokens = fullKpiId.split("\\.");
    if (tokens.length != 2) {
      throw new IllegalArgumentException("Invalid full kpi id format: " + fullKpiId);
    }
    return new FullKpiId(tokens[0], tokens[1]);
  }
}
