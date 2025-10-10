package com.asemicanalytics.core.dataframe;

import java.util.List;
import java.util.Map;

public class DataframeValidator {
  public static void validateListLengths(Map<String, List<String>> dimensions,
                                         Map<String, List<Number>> metrics) {
    if (dimensions.isEmpty() && metrics.isEmpty()) {
      return;
    }
    int expectedSize = dimensions.values().stream()
        .filter(list -> !list.isEmpty())
        .findFirst()
        .map(List::size)
        .orElseGet(() -> metrics.values().stream()
            .filter(list -> !list.isEmpty())
            .findFirst()
            .map(List::size)
            .orElse(0));
    boolean dimensionsValid = dimensions.entrySet().stream()
        .allMatch(e -> e.getValue().size() == expectedSize);
    boolean metricsValid = metrics.entrySet().stream()
        .allMatch(e -> e.getValue().size() == expectedSize);
    if (!dimensionsValid) {
      throw new IllegalArgumentException("Dimension lists have inconsistent lengths");
    }
    if (!metricsValid) {
      throw new IllegalArgumentException("Metric lists have inconsistent lengths");
    }
  }
}

