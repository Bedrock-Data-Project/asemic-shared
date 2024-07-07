package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class IfExpression implements Expression {

  private final BooleanExpression condition;
  private final Expression ifTrue;
  private final Expression ifFalse;

  public IfExpression(BooleanExpression condition, Expression ifTrue, Expression ifFalse) {
    this.condition = condition;
    this.ifTrue = ifTrue;
    this.ifFalse = ifFalse;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.ifExpression(
        condition.render(dialect),
        ifTrue.render(dialect),
        ifFalse.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    condition.swapTable(oldTable, newTable);
    ifTrue.swapTable(oldTable, newTable);
    ifFalse.swapTable(oldTable, newTable);
  }
}
