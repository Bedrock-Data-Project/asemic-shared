package com.asemicanalytics.sequence.sequence;

import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import java.util.Optional;

public record DomainStep(
    String columnSourceName,
    Optional<BooleanExpression> filter,
    Optional<String> alias) {
  public String name() {
    return alias.orElse(columnSourceName);
  }
}
