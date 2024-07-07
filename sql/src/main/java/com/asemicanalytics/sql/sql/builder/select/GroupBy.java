package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.ExpressionList;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class GroupBy implements Token {
  private final ExpressionList expressions;

  public GroupBy(ExpressionList expressions) {
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
