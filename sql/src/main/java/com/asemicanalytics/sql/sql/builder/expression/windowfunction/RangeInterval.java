package com.asemicanalytics.sql.sql.builder.expression.windowfunction;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;

public class RangeInterval implements Token {
  private final RangeType rangeType;
  private final RangeIntervalToken from;
  private final RangeIntervalToken to;

  public RangeInterval(RangeType rangeType, RangeIntervalToken from,
                       RangeIntervalToken rangeIntervalToken) {
    this.rangeType = rangeType;
    this.from = from;
    to = rangeIntervalToken;
  }

  @Override
  public String render(Dialect dialect) {
    return rangeType + " BETWEEN " + from.render(dialect) + " AND " + to.render(dialect);
  }
}
