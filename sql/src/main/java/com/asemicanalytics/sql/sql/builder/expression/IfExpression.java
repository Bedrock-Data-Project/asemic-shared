package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class IfExpression implements Expression {

  private final Expression condition;
  private final Expression ifTrue;
  private final Expression ifFalse;

  public IfExpression(Expression condition, Expression ifTrue, Expression ifFalse) {
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
