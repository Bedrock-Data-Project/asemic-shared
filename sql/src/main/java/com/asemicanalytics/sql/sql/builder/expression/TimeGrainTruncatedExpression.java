package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

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
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }
}
