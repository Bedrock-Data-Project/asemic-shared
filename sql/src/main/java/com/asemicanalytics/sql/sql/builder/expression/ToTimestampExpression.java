package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class ToTimestampExpression implements Expression {

  private final Expression expression;
  private final int shiftDays;

  public ToTimestampExpression(Expression expression, int shiftDays) {
    this.expression = expression;
    this.shiftDays = shiftDays;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.covertToTimestamp(expression.render(dialect), shiftDays);
  }

}
