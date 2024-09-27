package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;

public class FixedWindowColumn extends EntityProperty {
  private final EntityProperty sourceColumn;
  private final DateInterval dateInterval;
  private final WindowAggregationFunction windowAggregationFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.FIXED_WINDOW;
  }

  public FixedWindowColumn(
      Column column,
      EntityProperty sourceColumn,
      DateInterval dateInterval,
      WindowAggregationFunction windowAggregationFunction) {
    super(column);
    this.sourceColumn = sourceColumn;
    this.dateInterval = dateInterval;
    this.windowAggregationFunction = windowAggregationFunction;
  }

  public DisconnectedDateIntervals getMaterializedOn(
      TableReference tableReference,
      MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedOn(getId());
  }

  public DateInterval getDateInterval() {
    return dateInterval;
  }

  public WindowAggregationFunction getWindowAggregationFunction() {
    return windowAggregationFunction;
  }

  public EntityProperty getSourceColumn() {
    return sourceColumn;
  }
}
