package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TimeGrains;

public class TimeGrainTruncatedExpression implements Expression {

  private final Expression expression;
  private final TimeGrains timeGrain;
  private final int shiftDays;

  public TimeGrainTruncatedExpression(Expression expression, TimeGrains timeGrain, int shiftDays) {
    this.expression = expression;
    this.timeGrain = timeGrain;
    this.shiftDays = shiftDays;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.truncateTimestamp(expression.render(dialect), timeGrain, shiftDays);
  }

  @Override
  public String renderDefinition(Dialect dialect) {
    return Expression.super.renderDefinition(dialect);
  }
}
