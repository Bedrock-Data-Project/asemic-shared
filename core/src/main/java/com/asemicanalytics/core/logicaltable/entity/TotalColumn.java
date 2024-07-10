package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class TotalColumn extends Column {
  private final String sourceColumnId;
  private final String function;

  public TotalColumn(Column column, String sourceColumnId, String function) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.sourceColumnId = sourceColumnId;
    this.function = function;
  }

  public String getSourceColumnId() {
    return sourceColumnId;
  }

  public String getFunction() {
    return function;
  }
}
