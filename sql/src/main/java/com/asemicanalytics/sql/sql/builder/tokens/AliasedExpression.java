package com.asemicanalytics.sql.sql.builder.tokens;


import com.asemicanalytics.core.Dialect;

class AliasedExpression implements Expression {

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
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }

  @Override
  public String referenceInGroupByOrderBy(Dialect dialect) {
    return dialect.referenceAliasedExpression(expression.render(dialect), alias);
  }

  @Override
  public String renderDefinition(Dialect dialect) {
    // special case where the alias is the same as the column name
    if (expression instanceof TableColumn tableColumn && tableColumn.name().equals(alias)) {
      return expression.render(dialect);
    }

    return expression.render(dialect) + " AS " + dialect.columnIdentifier(alias);
  }

  @Override
  public AliasedExpression withAlias(String alias) {
    return new AliasedExpression(expression, alias);
  }

  public String alias() {
    return alias;
  }
}
