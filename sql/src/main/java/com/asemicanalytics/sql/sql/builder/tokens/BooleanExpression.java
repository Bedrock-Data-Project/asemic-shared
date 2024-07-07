package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.Map;

public class BooleanExpression extends TemplatedExpression {
  protected BooleanExpressionNode nextNode;

  BooleanExpression(TemplatedExpression expression) {
    super(expression.getExpression(), expression.getTemplateDict());
  }

  BooleanExpression(Expression expression) {
    super("{e}", TemplateDict.noMissing(Map.of("e", expression)));
  }

  protected BooleanExpression tail() {
    var current = this;
    while (current.nextNode != null) {
      current = current.nextNode.getExpression();
    }
    return current;
  }

  public BooleanExpression and(BooleanExpression expression) {
    var tail = tail();
    tail.nextNode = new BooleanExpressionNode(BooleanOperator.AND, expression);
    return this;
  }

  public BooleanExpression or(BooleanExpression expression) {
    var tail = tail();
    tail.nextNode = new BooleanExpressionNode(BooleanOperator.OR, expression);
    return this;
  }

  public Expression if_(Expression trueExpression, Expression falseExpression) {
    return new IfExpression(this, trueExpression, falseExpression);
  }

  protected String renderBaseExpression(Dialect dialect) {
    return super.render(dialect);
  }

  @Override
  public String render(Dialect dialect) {
    var tail = nextNode != null ? nextNode.render(dialect) : "";
    return renderBaseExpression(dialect) + tail;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    super.swapTable(oldTable, newTable);
    if (nextNode != null) {
      nextNode.swapTable(oldTable, newTable);
    }
  }
}
