package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.RelativeDaysInterval;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SlidingWindowColumn extends EntityProperty {
  private final EntityProperty sourceColumn;
  private final RelativeDaysInterval relativeDaysInterval;
  private final WindowAggregationFunction windowAggregationFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.SLIDING_WINDOW;
  }

  @Override
  public Set<String> referencedProperties() {
    return Set.of(sourceColumn.getId());
  }

  @Override
  public Map<EventLogicalTable, Set<String>> referencedEventParameters() {
    return Map.of();
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
