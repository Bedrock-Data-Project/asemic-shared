package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.column.Column;
import java.time.LocalDate;
import java.util.Optional;

public class FixedWindowColumn extends EntityProperty {
  private final EntityProperty sourceColumn;
  private final DatetimeInterval datetimeInterval;
  private final WindowAggregationFunction windowAggregationFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.FIXED_WINDOW;
  }

  public FixedWindowColumn(
      Column column,
      EntityProperty sourceColumn,
      DatetimeInterval datetimeInterval,
      WindowAggregationFunction windowAggregationFunction) {
    super(column);


    this.sourceColumn = sourceColumn;
    this.datetimeInterval = datetimeInterval;
    this.windowAggregationFunction = windowAggregationFunction;
  }

  public Optional<LocalDate> getMaterializedFrom(MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedFrom(getId());
  }

  public DatetimeInterval getDatetimeInterval() {
    return datetimeInterval;
  }

  public WindowAggregationFunction getWindowAggregationFunction() {
    return windowAggregationFunction;
  }

  public EntityProperty getSourceColumn() {
    return sourceColumn;
  }
}
