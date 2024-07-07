package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class Having implements Token {
  private final BooleanExpression expression;

  public Having(BooleanExpression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return "HAVING " + expression.render(dialect) + "\n";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }

}
