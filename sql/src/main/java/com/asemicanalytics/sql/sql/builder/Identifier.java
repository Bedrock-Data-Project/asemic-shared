package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;

public class Identifier implements Token {

  private final String identifier;

  public Identifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String render(Dialect dialect) {
    return identifier;
  }
}
