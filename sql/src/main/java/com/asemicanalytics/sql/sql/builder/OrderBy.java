package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;

public class OrderBy implements Token {
  private final ExpressionList expressions;
  private final boolean desc;

  public OrderBy(ExpressionList expressions, boolean desc) {
    this.expressions = expressions;
    this.desc = desc;
  }

  public OrderBy(ExpressionList expressions) {
    this(expressions, false);
  }

  @Override
  public String render(Dialect dialect) {

    return "ORDER BY\n  " + expressions.referenceInGroupByOrderBy(dialect)
        + (desc ? " DESC" : "") + "\n";
  }

  public ExpressionList expressions() {
    return expressions;
  }
}
