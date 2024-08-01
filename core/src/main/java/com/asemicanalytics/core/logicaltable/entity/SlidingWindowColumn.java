package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.RelativeDaysInterval;
import com.asemicanalytics.core.column.Column;
import java.time.LocalDate;
import java.util.Optional;

public class SlidingWindowColumn extends EntityProperty {
  private final EntityProperty sourceColumn;
  private final RelativeDaysInterval relativeDaysInterval;
  private final WindowAggregationFunction windowAggregationFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.SLIDING_WINDOW;
  }

  public enum WindowAggregationFunction {
    SUM,
    AVG,
    MIN,
    MAX,
  }

  public SlidingWindowColumn(
      Column column,
      EntityProperty sourceColumn,
      RelativeDaysInterval relativeDaysInterval,
      WindowAggregationFunction windowAggregationFunction) {
    super(column);

    if (relativeDaysInterval.from() == 0) {
      throw new IllegalArgumentException("Sliding window cannot start at 0");
    }
    this.sourceColumn = sourceColumn;
    this.relativeDaysInterval = relativeDaysInterval;
    this.windowAggregationFunction = windowAggregationFunction;
  }

  public Optional<LocalDate> getMaterializedFrom(MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedFrom(getId());
  }

  public RelativeDaysInterval getRelativeDaysInterval() {
    return relativeDaysInterval;
  }

  public WindowAggregationFunction getWindowAggregationFunction() {
    return windowAggregationFunction;
  }

  public EntityProperty getSourceColumn() {
    return sourceColumn;
  }
}
