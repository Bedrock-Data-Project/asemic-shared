package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaseExpression implements Expression {

  private final Optional<Expression> switchExpression;
  private List<CaseWhenThen> whenThens = new ArrayList<>();
  private Expression elseExpression;

  CaseExpression(
      Expression switchExpression) {
    this.switchExpression = Optional.of(switchExpression);
  }

  CaseExpression() {
    this.switchExpression = Optional.empty();
  }

  @Override
  public String render(Dialect dialect) {

    var whenThenExpressions = whenThens.stream()
        .map(whenThen -> whenThen.render(dialect))
        .reduce((a, b) -> a + " " + b)
        .orElseThrow();

    var elseExpression = dialect.caseElse(this.elseExpression != null
        ? this.elseExpression.render(dialect)
        : "NULL");

    return switchExpression
        .map(s -> dialect.caseExpression(
            s.render(dialect),
            whenThenExpressions,
            elseExpression))
        .orElseGet(() -> dialect.caseExpression(whenThenExpressions, elseExpression));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    switchExpression.ifPresent(s -> s.swapTable(oldTable, newTable));
    whenThens.forEach(whenThen -> whenThen.swapTable(oldTable, newTable));
    if (elseExpression != null) {
      elseExpression.swapTable(oldTable, newTable);
    }
  }

  public CaseExpression when(Expression when, Expression then) {
    whenThens.add(new CaseWhenThen(when, then));
    return this;
  }

  public CaseExpression else_(Expression elseExpression) {
    this.elseExpression = elseExpression;
    return this;
  }
}
