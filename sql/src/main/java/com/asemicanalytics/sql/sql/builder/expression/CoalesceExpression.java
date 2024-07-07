package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class CoalesceExpression extends FunctionExpression {

  public CoalesceExpression(ExpressionList arguments) {
    super("COALESCE", arguments);
  }

  @Override
  public String render(Dialect dialect) {
    if (arguments.expressions().size() == 1) {
      return arguments.render(dialect);
    }
    return super.render(dialect);
  }
}
