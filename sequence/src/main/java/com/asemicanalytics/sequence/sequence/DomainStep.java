package com.asemicanalytics.sequence.sequence;

import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import java.util.Optional;

public record DomainStep(
    String actionLogicalTableName,
    Optional<BooleanExpression> filter,
    Optional<String> alias) {
  public String name() {
    return alias.orElse(actionLogicalTableName);
  }
}
