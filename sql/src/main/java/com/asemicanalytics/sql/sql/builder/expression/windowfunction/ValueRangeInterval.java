package com.asemicanalytics.sql.sql.builder.expression.windowfunction;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
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

}
