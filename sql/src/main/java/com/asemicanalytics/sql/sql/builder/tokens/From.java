package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class From implements Token {
  private TableLike tableLike;

  From(TableLike tableLike) {
    this.tableLike = tableLike;
  }

  @Override
  public String render(Dialect dialect) {
    return "FROM " + tableLike.renderTableDeclaration(dialect);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    if (tableLike.equals(oldTable)) {
      tableLike = newTable;
    }
  }

  public TableLike table() {
    return tableLike;
  }
}
