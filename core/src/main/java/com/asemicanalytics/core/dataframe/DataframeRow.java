package com.asemicanalytics.core.dataframe;

import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedMap;
import java.util.TreeMap;

public record DataframeRow(
    SequencedMap<String, String> dimensions,
    SequencedMap<String, Number> metrics
) {
  public static DataframeRow fromIndex(
      Map<String, List<String>> dimensions,
      Map<String, List<Number>> metrics,
      int index) {
    SequencedMap<String, String> rowDimensions = new TreeMap<>();
    SequencedMap<String, Number> rowMetrics = new TreeMap<>();
    for (Map.Entry<String, List<String>> entry : dimensions.entrySet()) {
      rowDimensions.put(entry.getKey(), entry.getValue().get(index));
    }
    for (Map.Entry<String, List<Number>> entry : metrics.entrySet()) {
      rowMetrics.put(entry.getKey(), entry.getValue().get(index));
    }
    return new DataframeRow(rowDimensions, rowMetrics);
  }

  public String value(String dimensionOrMetricName) {
    if (dimensions.containsKey(dimensionOrMetricName)) {
      return dimensions.get(dimensionOrMetricName);
    } else if (metrics.containsKey(dimensionOrMetricName)) {
      Number metricValue = metrics.get(dimensionOrMetricName);
      return metricValue == null ? null : metricValue.toString();
    } else {
      throw new IllegalArgumentException("No such dimension or metric: " + dimensionOrMetricName);
    }
  }
}
