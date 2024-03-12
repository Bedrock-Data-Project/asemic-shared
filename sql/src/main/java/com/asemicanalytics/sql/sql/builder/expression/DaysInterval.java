package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class DaysInterval implements Expression {
  private final long days;

  public DaysInterval(long days) {
    this.days = days;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.intervalDays(days);
  }
}
