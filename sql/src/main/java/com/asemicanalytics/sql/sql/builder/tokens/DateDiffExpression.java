package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class DateDiffExpression implements Expression {
  private final Expression from;
  private final Expression to;

  DateDiffExpression(Expression from, Expression to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.dateDiff(from.render(dialect), to.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    from.swapTable(oldTable, newTable);
    to.swapTable(oldTable, newTable);
  }
}
