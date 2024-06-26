package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class From implements Token {
  private final TableLike tableLike;

  public From(TableLike tableLike) {
    this.tableLike = tableLike;
  }

  @Override
  public String render(Dialect dialect) {
    return "FROM " + tableLike.renderTableDeclaration(dialect);
  }

  public TableLike table() {
    return tableLike;
  }
}
