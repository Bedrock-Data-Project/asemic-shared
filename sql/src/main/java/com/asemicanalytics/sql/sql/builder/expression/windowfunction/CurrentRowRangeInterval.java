package com.asemicanalytics.sql.sql.builder.expression.windowfunction;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class CurrentRowRangeInterval implements RangeIntervalToken {

  @Override
  public String render(Dialect dialect) {
    return "CURRENT ROW";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }

}
