package com.asemicanalytics.sql.sql.builder.booleanexpression;


import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class BooleanExpressionNode implements Token {
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
