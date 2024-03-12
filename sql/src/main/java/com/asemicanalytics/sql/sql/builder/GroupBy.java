package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;

public class GroupBy implements Token {
  private final ExpressionList expressions;

  public GroupBy(ExpressionList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String render(Dialect dialect) {
    return "GROUP BY\n  " + expressions.referenceInGroupByOrderBy(dialect) + "\n";
  }

  public ExpressionList expressions() {
    return expressions;
  }
}
