
package com.asemicanalytics.sql.sql.builder.expression.casecondition;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.Expression;

public class CaseWhenThen implements Token {

  private final Expression when;
  private final Expression then;

  public CaseWhenThen(Expression when, Expression then) {
    this.when = when;
    this.then = then;
  }


  @Override
  public String render(Dialect dialect) {
    return dialect.caseWhenThen(when.render(dialect), then.render(dialect));
  }

}
