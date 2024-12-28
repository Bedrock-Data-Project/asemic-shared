package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.logicaltable.entity.EventColumn;
import java.util.List;

class FunctionExpression implements Expression {

  protected final String functionName;
  protected final ExpressionList arguments;

  public FunctionExpression(String functionName) {
    this.functionName = functionName;
    this.arguments = new ExpressionList(List.of());
  }

  public FunctionExpression(String functionName, Expression... expressions) {
    this.functionName = functionName;
    this.arguments = new ExpressionList(List.of(expressions), ", ");
  }

  @Override
  public String render(Dialect dialect) {
    if (this.functionName.equals(EventColumn.AggregateFunction.COUNT_DISTINCT.name())) {
      return "COUNT(DISTINCT " + arguments.render(dialect) + ")";
    }
    return functionName.toUpperCase() + "(" + arguments.render(dialect) + ")";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    arguments.swapTable(oldTable, newTable);
  }
}
