package com.asemicanalytics.core.kpi;

public record Unit(
    String symbol,
    boolean isPrefix
) {
  public static Unit dollar() {
    return new Unit("$", true);
  }

  public static Unit percent() {
    return new Unit("%", false);
  }

  public static Unit minute() {
    return new Unit("min", false);
  }
}
