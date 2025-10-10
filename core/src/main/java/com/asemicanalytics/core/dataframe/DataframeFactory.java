package com.asemicanalytics.core.dataframe;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlResultRow;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class DataframeFactory {
  public static Dataframe fromSqlResult(List<SqlResultRow> rows, List<String> columnNames,
                                        List<DataType> columnTypes) {
    if (columnNames.size() != columnTypes.size()) {
      throw new IllegalArgumentException("Column names and types must have the same length");
    }
    SequencedMap<String, List<String>> dimensions = new LinkedHashMap<>();
    SequencedMap<String, List<Number>> metrics = new LinkedHashMap<>();
    for (SqlResultRow row : rows) {
      for (int i = 0; i < columnNames.size(); i++) {
        String columnName = columnNames.get(i);
        DataType dataType = columnTypes.get(i);
        Object value = row.columns().get(i);
        switch (dataType) {
          case STRING, DATE, DATETIME, BOOLEAN ->
              dimensions.computeIfAbsent(columnName, k -> new ArrayList<>())
                  .add(value == null ? null : value.toString());
          case NUMBER, INTEGER -> metrics.computeIfAbsent(columnName, k -> new ArrayList<>())
              .add(value == null ? null : ((Number) value));
          default -> throw new IllegalArgumentException("Unsupported data type: " + dataType);
        }
      }
    }
    return new Dataframe(dimensions, metrics);
  }
}

