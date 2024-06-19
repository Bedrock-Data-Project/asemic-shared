package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class FirstAppearanceColumn extends Column {
  private final String firstAppearanceTableColumn;

  public FirstAppearanceColumn(Column column, String firstAppearanceTableColumn) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.firstAppearanceTableColumn = firstAppearanceTableColumn;
  }

  public String getFirstAppearanceTableColumn() {
    return firstAppearanceTableColumn;
  }
}
