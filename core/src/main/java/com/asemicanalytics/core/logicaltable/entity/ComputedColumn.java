package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;

public class ComputedColumn extends EntityProperty {
  private final String formula;

  public ComputedColumn(Column column, String formula) {
    super(column);
    this.formula = formula;
  }

  public String getFormula() {
    return formula;
  }

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.COMPUTED;
  }
}
