package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class RegistrationColumn extends EntityProperty {
  private final String registrationTableColumn;

  public RegistrationColumn(Column column, String firstAppearanceTableColumn) {
    super(column);
    this.registrationTableColumn = firstAppearanceTableColumn;
  }

  public String getRegistrationTableColumn() {
    return registrationTableColumn;
  }

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.REGISTRATION;
  }
}
