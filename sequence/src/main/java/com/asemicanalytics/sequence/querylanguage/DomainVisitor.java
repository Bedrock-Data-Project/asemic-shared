package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DomainVisitor extends QueryLanguageBaseVisitor<VisitorResult> {

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
        : Optional.of(new BooleanExpression(new ExpressionVisitor().visit(ctx.domainStepFilter())));
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
