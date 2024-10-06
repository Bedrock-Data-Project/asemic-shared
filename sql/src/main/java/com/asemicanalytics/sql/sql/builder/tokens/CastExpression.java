package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;

public class CastExpression implements Expression {
  private final Expression expression;
  private final DataType dataType;

  public CastExpression(Expression expression, DataType dataType) {
    this.expression = expression;
    this.dataType = dataType;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.cast(expression.render(dialect), dataType);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }
}
