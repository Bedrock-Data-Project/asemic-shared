package com.asemicanalytics.sql.sql.builder.tokens;


import com.asemicanalytics.core.Dialect;

class Qualify implements Token {
  private final BooleanExpression booleanExpression;

  public Qualify(BooleanExpression booleanExpression) {
    this.booleanExpression = booleanExpression;
  }

  @Override
  public String render(Dialect dialect) {
    return "QUALIFY " + booleanExpression.render(dialect);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    booleanExpression.swapTable(oldTable, newTable);
  }

  public BooleanExpression booleanExpression() {
    return booleanExpression;
  }
}
