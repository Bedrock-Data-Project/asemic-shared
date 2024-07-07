package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class TemplatedExpression implements Expression {
  protected final String expression;
  protected TemplateDict templateDict;

  public TemplatedExpression(String expression, TemplateDict templateDict) {
    this.expression = expression;
    this.templateDict = templateDict;
    Formatter.validate(expression, templateDict);
  }

  public String getExpression() {
    return expression;
  }

  public TemplateDict getTemplateDict() {
    return templateDict;
  }

  @Override
  public String render(Dialect dialect) {
    return Formatter.format(expression, templateDict, dialect);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    templateDict.swapTable(oldTable, newTable);
  }
}
