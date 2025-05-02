package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixedWindowColumn extends EntityProperty {
  private final EntityProperty sourceColumn;
  private final DateInterval dateInterval;
  private final WindowAggregationFunction windowAggregationFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.FIXED_WINDOW;
  }

  @Override
  public Set<String> referencedProperties() {
    return Set.of(sourceColumn.getId());
  }

  @Override
  public Map<EventLogicalTable, Set<String>> referencedEventParameters() {
    return Map.of();
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
