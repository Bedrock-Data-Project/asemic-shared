package com.asemicanalytics.sql.sql.builder.tokens;


import com.asemicanalytics.core.Dialect;

class Where implements Token {
  private final BooleanExpression booleanExpression;

  public Where(BooleanExpression booleanExpression) {
    this.booleanExpression = booleanExpression;
  }

  @Override
  public String render(Dialect dialect) {
    return "WHERE " + booleanExpression.render(dialect);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    booleanExpression.swapTable(oldTable, newTable);
  }

  public BooleanExpression booleanExpression() {
    return booleanExpression;
  }
}
