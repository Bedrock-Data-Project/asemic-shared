package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import java.util.Optional;

public class ActionColumn extends EntityProperty {
  private final ActionLogicalTable actionLogicalTable;
  private final Optional<String> where;
  private final String select;
  private final AggregateFunction aggregationFunction;
  private final String defaultValue;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.ACTION;
  }

  public enum AggregateFunction {
    COUNT,
    SUM,
    AVG,
    MIN,
    MAX,
    FIRST_VALUE,
    LAST_VALUE,
    NONE
  }

  public ActionColumn(
      Column column,
      ActionLogicalTable actionLogicalTable,
      Optional<String> where,
      String select,
      AggregateFunction aggregationFunction,
      String defaultValue) {
    super(column);
    this.actionLogicalTable = actionLogicalTable;
    this.where = where;
    this.select = select;
    this.aggregationFunction = aggregationFunction;
    this.defaultValue = defaultValue;
  }

  public ActionLogicalTable getActionLogicalTable() {
    return actionLogicalTable;
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
}
