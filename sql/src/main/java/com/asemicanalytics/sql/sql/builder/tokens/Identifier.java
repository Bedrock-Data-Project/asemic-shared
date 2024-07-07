package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class Identifier implements Expression {

  private final String identifier;

  public Identifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String render(Dialect dialect) {
    return identifier;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }
}
