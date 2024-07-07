package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class GenerateNumberArrayExpression implements Expression {
  private final Expression fromExpression;
  private final Expression toExpression;

  public GenerateNumberArrayExpression(Expression fromExpression, Expression toExpression) {
    this.fromExpression = fromExpression;
    this.toExpression = toExpression;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.generateNumberArray(
        fromExpression.render(dialect),
        toExpression.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    fromExpression.swapTable(oldTable, newTable);
    toExpression.swapTable(oldTable, newTable);
  }
}
