package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class DateAddExpression implements Expression {
  private final Expression dateExpression;
  private final int days;

  public DateAddExpression(Expression dateExpression, int days) {
    this.dateExpression = dateExpression;
    this.days = days;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.dateAdd(dateExpression.render(dialect), days);
  }
}
