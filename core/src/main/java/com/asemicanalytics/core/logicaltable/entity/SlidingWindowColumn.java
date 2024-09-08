package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.RelativeDaysInterval;
import com.asemicanalytics.core.TableReference;
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

  public SlidingWindowColumn(
      Column column,
      EntityProperty sourceColumn,
      RelativeDaysInterval relativeDaysInterval,
      WindowAggregationFunction windowAggregationFunction) {
    super(column);

    this.sourceColumn = sourceColumn;
    this.relativeDaysInterval = relativeDaysInterval;
    this.windowAggregationFunction = windowAggregationFunction;
  }

  public DisconnectedDateIntervals getMaterializedFrom(
      TableReference tableReference, MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedOn(getId());
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
