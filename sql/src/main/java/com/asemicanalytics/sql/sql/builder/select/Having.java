package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class Having implements Token {
  private final BooleanExpression expression;

  public Having(BooleanExpression expression) {
    this.expression = expression;
  }

  @Override
  public String render(Dialect dialect) {
    return "HAVING " + expression.render(dialect) + "\n";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }

}
