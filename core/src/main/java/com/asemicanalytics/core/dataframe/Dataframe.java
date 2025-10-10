package com.asemicanalytics.core.dataframe;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlResultRow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.SortedMap;
import java.util.function.Function;

public class Dataframe implements Iterable<DataframeRow> {
  private final SequencedMap<String, List<String>> dimensions;
  private final SequencedMap<String, List<Number>> metrics;

  public Dataframe() {
    this.dimensions = new LinkedHashMap<>();
    this.metrics = new LinkedHashMap<>();
    DataframeValidator.validateListLengths(dimensions, metrics);
  }

  public Dataframe(SequencedMap<String, List<String>> dimensions,
                   SequencedMap<String, List<Number>> metrics) {
    this.dimensions = dimensions;
    this.metrics = metrics;
    DataframeValidator.validateListLengths(dimensions, metrics);
  }

  public static Dataframe fromSqlResult(List<SqlResultRow> rows, List<String> columnNames,
                                        List<DataType> columnTypes) {
    return DataframeFactory.fromSqlResult(rows, columnNames, columnTypes);
  }

  @Override
  public Iterator<DataframeRow> iterator() {
    return new DataframeIterator(dimensions, metrics);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dataframe that = (Dataframe) o;
    return java.util.Objects.equals(dimensions, that.dimensions)
        && java.util.Objects.equals(metrics, that.metrics);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(dimensions, metrics);
  }

  public void addDimension(String name, Function<DataframeRow, String> extractor) {
    List<String> values = new ArrayList<>();
    for (DataframeRow row : this) {
      values.add(extractor.apply(row));
    }
    dimensions.put(name, values);
  }

  public void addMetric(String name, Function<DataframeRow, Number> extractor) {
    List<Number> values = new ArrayList<>();
    for (DataframeRow row : this) {
      values.add(extractor.apply(row));
    }
    metrics.put(name, values);
  }

  public String getDimension(String name, int index) {
    List<String> values = dimensions.get(name);
    if (values == null || index < 0 || index >= values.size()) {
      throw new IllegalArgumentException(
          "No such dimension or invalid index: " + name + ", " + index);
    }
    return values.get(index);
  }

  public Number getMetric(String name, int index) {
    List<Number> values = metrics.get(name);
    if (values == null || index < 0 || index >= values.size()) {
      throw new IllegalArgumentException("No such metric or invalid index: " + name + ", " + index);
    }
    return values.get(index);
  }

  public String getValue(String name, int index) {
    if (dimensions.containsKey(name)) {
      return getDimension(name, index);
    } else if (metrics.containsKey(name)) {
      Number value = getMetric(name, index);
      return value == null ? null : value.toString();
    } else {
      throw new IllegalArgumentException("No such dimension or metric: " + name);
    }
  }

  public List<String> getDimensionNames() {
    return new ArrayList<>(dimensions.keySet());
  }

  public List<String> getMetricNames() {
    return new ArrayList<>(metrics.keySet());
  }

  public int size() {
    if (!dimensions.isEmpty()) {
      return dimensions.values().iterator().next().size();
    } else if (!metrics.isEmpty()) {
      return metrics.values().iterator().next().size();
    } else {
      return 0;
    }
  }
}
