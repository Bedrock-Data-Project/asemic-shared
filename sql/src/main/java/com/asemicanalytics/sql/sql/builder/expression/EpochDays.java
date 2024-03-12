package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class EpochDays implements Expression {
  private final Expression expression;

  public EpochDays(Expression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.epochDays(expression.render(dialect));
  }
}
