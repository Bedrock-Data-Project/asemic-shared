package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.List;

public class CaseExpression implements Expression {

  private final Expression switchExpression;
  private List<CaseWhenThen> whenThens = new ArrayList<>();
  private Expression elseExpression;

  CaseExpression(
      Expression switchExpression) {
    this.switchExpression = switchExpression;
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

    return dialect.caseExpression(
        switchExpression.render(dialect),
        whenThenExpressions,
        elseExpression);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    switchExpression.swapTable(oldTable, newTable);
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
