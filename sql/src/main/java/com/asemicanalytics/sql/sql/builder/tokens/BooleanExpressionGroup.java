package com.asemicanalytics.sql.sql.builder.tokens;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;

import com.asemicanalytics.core.Dialect;

class BooleanExpressionGroup extends BooleanExpression {

  private BooleanExpression head;

  public BooleanExpressionGroup(BooleanExpression head) {
    super(parse("", TemplateDict.empty()));
    this.head = head;
  }

  @Override
  public String render(Dialect dialect) {
    var tail = nextNode != null ? nextNode.render(dialect) : "";
    var renderedHead = head.render(dialect);
    if (head.nextNode != null) {
      renderedHead = "(" + renderedHead + ")";
    }
    return renderedHead + tail;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    super.swapTable(oldTable, newTable);
    this.head.swapTable(oldTable, newTable);
  }
}
