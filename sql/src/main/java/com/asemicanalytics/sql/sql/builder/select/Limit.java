package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class Limit implements Token {
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
