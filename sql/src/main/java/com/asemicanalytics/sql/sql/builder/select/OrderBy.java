package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.ExpressionList;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class OrderBy implements Token {
  private final ExpressionList expressions;
  private final boolean desc;

  public OrderBy(ExpressionList expressions, boolean desc) {
    this.expressions = expressions;
    this.desc = desc;
  }

  public OrderBy(ExpressionList expressions) {
    this(expressions, false);
  }

  @Override
  public String render(Dialect dialect) {

    return "ORDER BY\n  " + expressions.referenceInGroupByOrderBy(dialect)
        + (desc ? " DESC" : "") + "\n";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expressions.swapTable(oldTable, newTable);
  }

  public ExpressionList expressions() {
    return expressions;
  }
}
