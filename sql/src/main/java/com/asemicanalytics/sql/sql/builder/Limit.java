package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;

public class Limit implements Token {
  private final int limit;

  public Limit(int limit) {
    this.limit = limit;
  }

  @Override
  public String render(Dialect dialect) {
    return "LIMIT " + limit;
  }
}
