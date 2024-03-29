package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;

public class TemplatedExpression implements Expression {
  protected final String expression;
  protected TemplateDict templateDict;

  public TemplatedExpression(String expression, TemplateDict templateDict) {
    this.expression = expression;
    this.templateDict = templateDict;
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
}
