package com.asemicanalytics.sql.sql.builder.expression.windowfunction;

import com.asemicanalytics.core.Dialect;

public class CurrentRowRangeInterval implements RangeIntervalToken {

  @Override
  public String render(Dialect dialect) {
    return "CURRENT ROW";
  }

}
