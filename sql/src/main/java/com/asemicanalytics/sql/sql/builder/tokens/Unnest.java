package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.sql.builder.ContentHashDialect;
import java.util.List;
import java.util.Optional;

class Unnest implements TableLike, Expression {
  private final Expression arrayExpression;
  private final String alias;

  public Unnest(Expression arrayExpression, String alias) {
    this.arrayExpression = arrayExpression;
    this.alias = alias;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.tableIdentifier(TableReference.of(alias));
  }

  @Override
  public String renderTableDeclaration(Dialect dialect) {
    return "UNNEST(" + arrayExpression.render(dialect) + ") AS " + dialect.unnestIdentifier(alias);
  }

  @Override
  public String tableName() {
    return alias;
  }

  @Override
  public Optional<Cte> getDependantCte() {
    return Optional.empty();
  }

  @Override
  public List<String> columnNames() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String contentHash() {
    return renderTableDeclaration(new ContentHashDialect());
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    arrayExpression.swapTable(oldTable, newTable);
  }
}
