package com.asemicanalytics.core.datasource.userwide;


import com.asemicanalytics.core.column.Column;

public class RegistrationColumn extends Column {
  private final String registrationTableColumn;

  public RegistrationColumn(Column column, String registrationTableColumn) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.registrationTableColumn = registrationTableColumn;
  }

  public String getRegistrationTableColumn() {
    return registrationTableColumn;
  }
}
