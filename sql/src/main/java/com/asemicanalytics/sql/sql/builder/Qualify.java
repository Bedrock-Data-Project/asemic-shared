package com.asemicanalytics.sql.sql.builder;


import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;

public class Qualify implements Token {
  private final BooleanExpression booleanExpression;

  public Qualify(BooleanExpression booleanExpression) {
    this.booleanExpression = booleanExpression;
  }

  @Override
  public String render(Dialect dialect) {
    return "QUALIFY " + booleanExpression.render(dialect);
  }

  public BooleanExpression booleanExpression() {
    return booleanExpression;
  }
}
