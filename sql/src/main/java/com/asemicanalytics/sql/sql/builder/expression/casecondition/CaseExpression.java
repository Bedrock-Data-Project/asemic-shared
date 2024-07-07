package com.asemicanalytics.sql.sql.builder.expression.casecondition;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.List;

public class CaseExpression implements Expression {

  private final Expression switchExpression;
  private final List<CaseWhenThen> whenThens;
  private final Expression elseExpression;

  public CaseExpression(
      Expression switchExpression, List<CaseWhenThen> whenThens, Expression elseExpression) {
    this.switchExpression = switchExpression;
    this.whenThens = whenThens;
    this.elseExpression = elseExpression;
  }

  public CaseExpression(
      Expression switchExpression, List<CaseWhenThen> whenThens) {
    this.switchExpression = switchExpression;
    this.whenThens = whenThens;
    this.elseExpression = null;
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
}
