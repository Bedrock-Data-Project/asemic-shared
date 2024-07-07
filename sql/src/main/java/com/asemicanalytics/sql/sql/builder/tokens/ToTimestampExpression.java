package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class ToTimestampExpression implements Expression {

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

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }

}
