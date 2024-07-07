package com.asemicanalytics.sql.sql.builder.tokens;


import com.asemicanalytics.core.Dialect;

class BooleanExpressionNode implements Token {
  private final BooleanOperator operator;
  private final BooleanExpression expression;

  public BooleanExpressionNode(BooleanOperator operator, BooleanExpression expression) {
    this.operator = operator;
    this.expression = expression;
  }

  public BooleanExpression getExpression() {
    return expression;
  }

  @Override
  public String render(Dialect dialect) {
    return " " + operator + " " + expression.render(dialect);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expression.swapTable(oldTable, newTable);
  }
}
