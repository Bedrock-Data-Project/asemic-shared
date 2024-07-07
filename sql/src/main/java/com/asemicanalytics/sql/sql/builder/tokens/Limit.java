package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class Limit implements Token {
  private final int limit;

  public Limit(int limit) {
    this.limit = limit;
  }

  @Override
  public String render(Dialect dialect) {
    return "LIMIT " + limit;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }
}
