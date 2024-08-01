package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class FirstAppearanceColumn extends EntityProperty {
  private final String firstAppearanceTableColumn;

  public FirstAppearanceColumn(Column column, String firstAppearanceTableColumn) {
    super(column);
    this.firstAppearanceTableColumn = firstAppearanceTableColumn;
  }

  public String getFirstAppearanceTableColumn() {
    return firstAppearanceTableColumn;
  }

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.FIRST_APPEARANCE;
  }
}
