package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;

class Constant implements Expression {
  private final String value;
  private final DataType dataType;

  public Constant(String value, DataType dataType) {
    this.value = value;
    this.dataType = dataType;
  }

  @Override
  public String render(Dialect dialect) {
    if (value == null) {
      return "NULL";
    }
    if (dataType == null) {
      return value;
    }
    return dialect.constant(value, dataType);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }
}
