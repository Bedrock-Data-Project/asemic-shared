package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class TotalColumn extends Column {
  private final String sourceColumnId;
  private final String mergeExpression;

  public TotalColumn(Column column, String sourceColumnId, String mergeExpression) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.sourceColumnId = sourceColumnId;
    this.mergeExpression = mergeExpression;
  }

  public String getSourceColumnId() {
    return sourceColumnId;
  }

  public String getMergeExpression() {
    return mergeExpression;
  }
}
