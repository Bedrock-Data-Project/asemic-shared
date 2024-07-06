package com.asemicanalytics.sql.sql.builder.tablelike;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.sql.builder.ContentHashDialect;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import java.util.Optional;

public class Unnest implements TableLike, Expression {
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
  public Optional<Cte> getDependantCte() {
    return Optional.empty();
  }

  @Override
  public String contentHash() {
    return renderTableDeclaration(new ContentHashDialect());
  }
}
