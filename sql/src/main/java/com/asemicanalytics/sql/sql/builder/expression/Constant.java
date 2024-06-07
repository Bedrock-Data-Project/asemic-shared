package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import java.time.LocalDate;

public class Constant implements Expression {
  private final String value;
  private final DataType dataType;

  public Constant(String value, DataType dataType) {
    this.value = value;
    this.dataType = dataType;
  }

  public static Constant ofInt(long value) {
    return new Constant(Long.toString(value), DataType.INTEGER);
  }

  public static Constant ofString(String value) {
    return new Constant(value, DataType.STRING);
  }

  public static Constant ofBoolean(boolean value) {
    return new Constant(Boolean.toString(value).toUpperCase(), DataType.BOOLEAN);
  }

  public static Constant ofDate(LocalDate value) {
    return new Constant(value.toString(), DataType.DATE);
  }

  public static Expression ofNull() {
    return new Constant(null, null);
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
}
