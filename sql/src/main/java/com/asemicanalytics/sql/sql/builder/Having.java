package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;

public class Having implements Token {
  private final BooleanExpression expression;

  public Having(BooleanExpression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return "HAVING " + expression.render(dialect) + "\n";
  }

}
