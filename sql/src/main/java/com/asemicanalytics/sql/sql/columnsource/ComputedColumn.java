package com.asemicanalytics.sql.sql.columnsource;

import com.asemicanalytics.core.column.Column;

public class ComputedColumn extends Column {
  private final String formula;

  public ComputedColumn(Column column, String formula) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy());
    this.formula = formula;
  }

  public String getFormula() {
    return formula;
  }
}
