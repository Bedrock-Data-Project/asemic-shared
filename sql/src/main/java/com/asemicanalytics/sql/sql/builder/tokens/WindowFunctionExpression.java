package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WindowFunctionExpression implements Expression {

  private final Expression function;
  private ExpressionList partitionBy = new ExpressionList();
  private ExpressionList orderBy = new ExpressionList();
  private Optional<RangeInterval> rangeInterval = Optional.empty();

  WindowFunctionExpression(
      Expression function) {
    this.function = function;
  }

  public WindowFunctionExpression partitionBy(Expression... partitionBy) {
    this.partitionBy = ExpressionList.inline(partitionBy);
    return this;
  }

  public WindowFunctionExpression partitionBy(List<Expression> partitionBy) {
    this.partitionBy = ExpressionList.inline(partitionBy);
    return this;
  }

  public WindowFunctionExpression orderBy(Expression... orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    return this;
  }

  public WindowFunctionExpression orderBy(List<Expression> orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    return this;
  }

  public WindowFunctionExpression rangeInterval(RangeInterval rangeInterval) {
    this.rangeInterval = Optional.of(rangeInterval);
    return this;
  }

  @Override
  public String render(Dialect dialect) {
    List<String> overTokens = new ArrayList<>();
    if (!partitionBy.isEmpty()) {
      overTokens.add("PARTITION BY " + this.partitionBy.render(dialect));
    }
    if (!orderBy.isEmpty()) {
      overTokens.add("ORDER BY " + this.orderBy.render(dialect));
    }
    rangeInterval.ifPresent(interval -> overTokens.add(interval.render(dialect)));
    return function.render(dialect) + " OVER (" + String.join(" ", overTokens) + ")";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    function.swapTable(oldTable, newTable);
    partitionBy.swapTable(oldTable, newTable);
    orderBy.swapTable(oldTable, newTable);
  }
}
