package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import java.util.List;

public class FunctionExpression implements Expression {

  protected final String functionName;
  protected final ExpressionList arguments;

  public FunctionExpression(String functionName) {
    this.functionName = functionName;
    this.arguments = new ExpressionList(List.of());
  }

  public FunctionExpression(String functionName, ExpressionList arguments) {
    this.functionName = functionName;
    this.arguments = arguments;
  }

  public FunctionExpression(String functionName, Expression expression) {
    this.functionName = functionName;
    this.arguments = new ExpressionList(expression);
  }

  @Override
  public String render(Dialect dialect) {
    return functionName.toUpperCase() + "(" + arguments.render(dialect) + ")";
  }

  @Override
  public String renderDefinition(Dialect dialect) {
    return Expression.super.renderDefinition(dialect);
  }
}
