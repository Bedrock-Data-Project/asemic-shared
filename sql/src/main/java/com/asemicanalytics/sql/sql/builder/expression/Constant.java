package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;

public class Constant implements Expression {
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
    return dialect.constant(value, dataType);
  }
}
