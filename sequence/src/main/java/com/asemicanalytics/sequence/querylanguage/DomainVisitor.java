package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DomainVisitor extends QueryLanguageBaseVisitor<VisitorResult> {

  private final Map<String, ColumnSource> stepColumnSources;

  public DomainVisitor(Map<String, ColumnSource> stepColumnSources) {
    this.stepColumnSources = stepColumnSources;
  }

  @Override
  public VisitorResult visitDomainStatement(QueryLanguageParser.DomainStatementContext ctx) {
    return visitDomainSteps(ctx.domainSteps());
  }

  @Override
  public VisitorResult visitDemainStep(QueryLanguageParser.DemainStepContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    String name = ctx.NAME().getText();
    Optional<BooleanExpression> filter = ctx.domainStepFilter() == null
        ? Optional.empty()
        : Optional.of(new BooleanExpression(new ExpressionVisitor(
        this.stepColumnSources.get(name).table()
    ).visit(ctx.domainStepFilter())));
    Optional<String> alias = ctx.domainStepAlias() == null
        ? Optional.empty()
        : Optional.of(new AliasVisitor().visit(ctx.domainStepAlias()));

    result.getDomain().put(name, new DomainStep(name, filter, alias));
    return result;
  }

  @Override
  public VisitorResult visitDomainSteps(QueryLanguageParser.DomainStepsContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    for (var domain : ctx.demainStep()) {
      result.merge(visitDemainStep(domain));
    }
    return result;
  }
}
