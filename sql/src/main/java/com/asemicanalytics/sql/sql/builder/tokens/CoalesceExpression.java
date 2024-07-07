package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class CoalesceExpression extends FunctionExpression {

  public CoalesceExpression(Expression... expressions) {
    super("COALESCE", expressions);
  }

  @Override
  public String render(Dialect dialect) {
    if (arguments.expressions().size() == 1) {
      return arguments.render(dialect);
    }
    return super.render(dialect);
  }
}
