package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class DaysInterval implements Expression {
  private final long days;

  public DaysInterval(long days) {
    this.days = days;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.intervalDays(days);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }
}
