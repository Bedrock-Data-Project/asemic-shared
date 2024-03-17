package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class ToUnixTimestamp implements Expression {

  private final Expression expression;

  public ToUnixTimestamp(Expression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.epochSeconds(expression.render(dialect));
  }

}
