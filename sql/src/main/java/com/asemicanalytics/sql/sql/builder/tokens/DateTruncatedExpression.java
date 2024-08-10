package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TimeGrains;

class DateTruncatedExpression implements Expression {

  private final Expression expression;
  private final TimeGrains timeGrain;

  public DateTruncatedExpression(Expression expression, TimeGrains timeGrain) {
    this.expression = expression;
    this.timeGrain = timeGrain;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.truncateDate(expression.render(dialect), timeGrain);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }
}
