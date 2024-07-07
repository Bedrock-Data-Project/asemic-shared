package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class From implements Token {
  private TableLike tableLike;

  public From(TableLike tableLike) {
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
