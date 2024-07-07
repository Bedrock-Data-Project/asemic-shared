package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class DateAddExpression implements Expression {
  private final Expression dateExpression;
  private final Expression daysExpression;

  public DateAddExpression(Expression dateExpression, int days) {
    this.dateExpression = dateExpression;
    this.daysExpression = Constant.ofInt(days);
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
