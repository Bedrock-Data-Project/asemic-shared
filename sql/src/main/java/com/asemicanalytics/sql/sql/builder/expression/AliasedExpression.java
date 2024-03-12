package com.asemicanalytics.sql.sql.builder.expression;


import com.asemicanalytics.core.Dialect;

public class AliasedExpression implements Expression {

  private final Expression expression;
  private final String alias;

  public AliasedExpression(Expression expression, String alias) {
    this.expression = expression;
    this.alias = alias;
  }

  @Override
  public String render(Dialect dialect) {
    return expression.render(dialect);
  }

  @Override
  public String referenceInGroupByOrderBy(Dialect dialect) {
    return dialect.referenceAliasedExpression(expression.render(dialect), alias);
  }

  @Override
  public String renderDefinition(Dialect dialect) {
    return expression.render(dialect) + " AS " + dialect.columnIdentifier(alias);
  }

  @Override
  public AliasedExpression withAlias(String alias) {
    return new AliasedExpression(expression, alias);
  }
}
