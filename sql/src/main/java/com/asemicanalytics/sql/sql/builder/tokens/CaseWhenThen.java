package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class CaseWhenThen implements Token {

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

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    when.swapTable(oldTable, newTable);
    then.swapTable(oldTable, newTable);
  }

}
