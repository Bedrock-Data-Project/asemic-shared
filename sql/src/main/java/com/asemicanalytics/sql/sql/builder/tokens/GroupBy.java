package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class GroupBy implements Token {
  private final ExpressionList expressions;

  GroupBy(ExpressionList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String render(Dialect dialect) {
    return "GROUP BY\n  " + expressions.referenceInGroupByOrderBy(dialect) + "\n";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expressions.swapTable(oldTable, newTable);
  }

  public ExpressionList expressions() {
    return expressions;
  }
}
