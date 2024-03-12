package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import java.util.List;

public class Select implements Token {
  private final ExpressionList expressions;

  public Select(ExpressionList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String render(Dialect dialect) {
    return "SELECT\n  " + expressions.renderDefinition(dialect) + "\n";
  }

  public void addExpression(Expression expression) {
    expressions.add(expression);
  }

  public void popExpression() {
    expressions.pop();
  }

  public void merge(Select select) {
    expressions.merge(select.expressions);
  }

  public List<Expression> expressions() {
    return expressions.expressions();
  }
}
