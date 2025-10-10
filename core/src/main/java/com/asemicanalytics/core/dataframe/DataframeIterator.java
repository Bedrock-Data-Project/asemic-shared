package com.asemicanalytics.core.dataframe;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SequencedMap;

public class DataframeIterator implements Iterator<DataframeRow> {
  private final SequencedMap<String, List<String>> dimensions;
  private final SequencedMap<String, List<Number>> metrics;
  private final int rowCount;
  private int currentIndex = 0;

  public DataframeIterator(SequencedMap<String, List<String>> dimensions,
                           SequencedMap<String, List<Number>> metrics) {
    this.dimensions = dimensions;
    this.metrics = metrics;
    if (!dimensions.isEmpty()) {
      this.rowCount = dimensions.values().iterator().next().size();
    } else if (!metrics.isEmpty()) {
      this.rowCount = metrics.values().iterator().next().size();
    } else {
      this.rowCount = 0;
    }
  }

  @Override
  public boolean hasNext() {
    return currentIndex < rowCount;
  }

  @Override
  public DataframeRow next() {
    if (!hasNext()) {
      throw new NoSuchElementException("No more elements in Dataframe");
    }
    DataframeRow row = DataframeRow.fromIndex(dimensions, metrics, currentIndex);
    currentIndex++;
    return row;
  }
}
