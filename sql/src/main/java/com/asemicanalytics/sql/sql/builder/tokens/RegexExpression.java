package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class RegexExpression implements Expression {

  private final Expression expression;
  private final String regex;

  public RegexExpression(Expression expression, String regex) {
    this.expression = expression;
    this.regex = regex;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.matchesRegex(expression.render(dialect), regex);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }

}
