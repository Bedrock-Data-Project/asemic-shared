package com.asemicanalytics.sql.sql.builder.select;


import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class Qualify implements Token {
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
