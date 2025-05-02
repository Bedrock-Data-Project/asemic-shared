package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LifetimeColumn extends EntityProperty {

  private final EntityProperty sourceColumn;
  private final MergeFunction mergeFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.LIFETIME;
  }

  @Override
  public Set<String> referencedProperties() {
    return Set.of(sourceColumn.getId());
  }

  @Override
  public Map<EventLogicalTable, Set<String>> referencedEventParameters() {
    return Map.of();
  }

  public enum MergeFunction {
    SUM,
    MIN,
    MAX,
    FIRST_VALUE,
    LAST_VALUE,
  }

  public LifetimeColumn(Column column, EntityProperty sourceColumn, MergeFunction mergeFunction) {
    super(column);
    this.sourceColumn = sourceColumn;
    this.mergeFunction = mergeFunction;
  }

  public EntityProperty getSourceColumn() {
    return sourceColumn;
  }

  public MergeFunction getMergeFunction() {
    return mergeFunction;
  }

  public DisconnectedDateIntervals getMaterializedOn(
      MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedOn(getId());
  }
}
