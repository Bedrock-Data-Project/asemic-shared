package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

public class CurrentRowRangeInterval implements RangeIntervalToken {

  @Override
  public String render(Dialect dialect) {
    return "CURRENT ROW";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }

}
