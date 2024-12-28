package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.Optional;

public class EventColumn extends EntityProperty {
  private final EventLogicalTable eventLogicalTable;
  private final Optional<String> where;
  private final String select;
  private final AggregateFunction aggregationFunction;
  private final String defaultValue;
  private final boolean generated;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.EVENT;
  }

  public enum AggregateFunction {
    COUNT,
    COUNT_DISTINCT,
    SUM,
    AVG,
    MIN,
    MAX,
    FIRST_VALUE,
    LAST_VALUE,
    NONE
  }

  public EventColumn(
      Column column,
      EventLogicalTable eventLogicalTable,
      Optional<String> where,
      String select,
      AggregateFunction aggregationFunction,
      String defaultValue) {
    this(column, eventLogicalTable, where, select, aggregationFunction, defaultValue, false);
  }

  public EventColumn(
      Column column,
      EventLogicalTable eventLogicalTable,
      Optional<String> where,
      String select,
      AggregateFunction aggregationFunction,
      String defaultValue,
      boolean generated) {
    super(column);
    this.eventLogicalTable = eventLogicalTable;
    this.where = where;
    this.select = select;
    this.aggregationFunction = aggregationFunction;
    this.defaultValue = defaultValue;
    this.generated = generated;
  }

  public EventLogicalTable getEventLogicalTable() {
    return eventLogicalTable;
  }

  public Optional<String> getWhere() {
    return where;
  }

  public String getSelect() {
    return select;
  }

  public DisconnectedDateIntervals getMaterializedOn(
      MaterializedColumnRepository materializedFrom) {
    return materializedFrom.materializedOn(getId());
  }

  public AggregateFunction getAggregationFunction() {
    return aggregationFunction;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean isGenerated() {
    return generated;
  }
}
