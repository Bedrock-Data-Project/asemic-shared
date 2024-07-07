package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.Optional;

public class ValueRangeInterval implements RangeIntervalToken {

  private final Optional<Expression> value;
  private final WindowFunctionBounds bounds;

  public ValueRangeInterval(Expression value, WindowFunctionBounds bounds) {
    this.value = Optional.of(value);
    this.bounds = bounds;
  }

  public ValueRangeInterval(WindowFunctionBounds bounds) {
    this.value = Optional.empty();
    this.bounds = bounds;
  }

  public ValueRangeInterval(Optional<Expression> value, WindowFunctionBounds bounds) {
    this.value = value;
    this.bounds = bounds;
  }

  @Override
  public String render(Dialect dialect) {
    return value.map(v -> v.render(dialect)).orElse("UNBOUNDED") + " " + bounds.name();
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    value.ifPresent(v -> v.swapTable(oldTable, newTable));
  }

}
