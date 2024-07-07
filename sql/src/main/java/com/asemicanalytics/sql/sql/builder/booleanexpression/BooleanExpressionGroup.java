package com.asemicanalytics.sql.sql.builder.booleanexpression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class BooleanExpressionGroup extends BooleanExpression {

  private BooleanExpression head;

  public BooleanExpressionGroup(BooleanExpression head) {
    super(new TemplatedExpression("", TemplateDict.empty()));
    this.head = head;
  }

  @Override
  public String render(Dialect dialect) {
    var tail = nextNode != null ? nextNode.render(dialect) : "";
    return "(" + head.render(dialect) + ")" + tail;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    super.swapTable(oldTable, newTable);
    this.head.swapTable(oldTable, newTable);
  }
}
