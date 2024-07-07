package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class EpochDays implements Expression {
  private final Expression expression;

  public EpochDays(Expression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.epochDays(expression.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }
}
