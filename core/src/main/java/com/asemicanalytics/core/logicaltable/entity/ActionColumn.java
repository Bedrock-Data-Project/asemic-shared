package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import java.time.LocalDate;
import java.util.Optional;

public class ActionColumn extends Column {
  private final ActionLogicalTable actionLogicalTable;
  private final Optional<String> where;
  private final String aggregationTableExpression;
  private final Optional<LocalDate> materializedFrom;
  private final String missingValue;
  private final boolean canMaterialize;

  public ActionColumn(
      Column column,
      ActionLogicalTable actionLogicalTable,
      Optional<String> where,
      String aggregationTableExpression,
      Optional<LocalDate> materializedFrom,
      String missingValue,
      boolean canMaterialize) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.actionLogicalTable = actionLogicalTable;
    this.where = where;
    this.aggregationTableExpression = aggregationTableExpression;
    this.materializedFrom = materializedFrom;
    this.missingValue = missingValue;
    this.canMaterialize = canMaterialize;
  }

  public ActionColumn(
      Column column,
      ActionLogicalTable actionLogicalTable,
      Optional<String> where,
      String aggregationTableExpression,
      Optional<LocalDate> materializedFrom,
      String missingValue
  ) {
    this(column, actionLogicalTable, where, aggregationTableExpression, materializedFrom,
        missingValue, true);
  }

  public ActionLogicalTable getActionLogicalTable() {
    return actionLogicalTable;
  }

  public Optional<String> getWhere() {
    return where;
  }

  public String getAggregationTableExpression() {
    return aggregationTableExpression;
  }

  public Optional<LocalDate> getMaterializedFrom() {
    return materializedFrom;
  }

  public boolean canMaterialize() {
    return canMaterialize;
  }

  public String getMissingValue() {
    return missingValue;
  }
}
