package com.asemicanalytics.sql.sql.builder.tokens;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;

import com.asemicanalytics.core.Dialect;

class DateAddExpression implements Expression {
  private final Expression dateExpression;
  private final Expression daysExpression;

  public DateAddExpression(Expression dateExpression, int days) {
    this.dateExpression = dateExpression;
    this.daysExpression = int_(days);
  }

  public DateAddExpression(Expression dateExpression, Expression days) {
    this.dateExpression = dateExpression;
    this.daysExpression = days;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.dateAdd(dateExpression.render(dialect), daysExpression.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    dateExpression.swapTable(oldTable, newTable);
    daysExpression.swapTable(oldTable, newTable);
  }
}
