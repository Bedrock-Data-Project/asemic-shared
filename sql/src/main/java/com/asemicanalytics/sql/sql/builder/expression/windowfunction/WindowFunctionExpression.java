package com.asemicanalytics.sql.sql.builder.expression.windowfunction;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.ExpressionList;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WindowFunctionExpression implements Expression {

  private final Expression function;
  private final ExpressionList partitionBy;
  private final ExpressionList orderBy;
  private final Optional<RangeInterval> rangeInterval;

  public WindowFunctionExpression(
      Expression function, ExpressionList partitionBy, ExpressionList orderBy,
      Optional<RangeInterval> rangeInterval) {
    this.function = function;
    this.partitionBy = partitionBy;
    this.orderBy = orderBy;
    this.rangeInterval = rangeInterval;
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
    if (rangeInterval.isPresent()) {
      overTokens.add(rangeInterval.get().render(dialect));
    }
    return function.render(dialect) + " OVER (" + String.join(" ", overTokens) + ")";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    function.swapTable(oldTable, newTable);
    partitionBy.swapTable(oldTable, newTable);
    orderBy.swapTable(oldTable, newTable);
  }
}
