package com.asemicanalytics.sequence.querylanguage;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.table;

import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sql.sql.builder.tokens.BooleanExpression;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DomainVisitor extends QueryLanguageBaseVisitor<VisitorResult> {

  private final Map<String, EventLogicalTable> stepLogicalTables;

  public DomainVisitor(Map<String, EventLogicalTable> stepLogicalTables) {
    this.stepLogicalTables = stepLogicalTables;
  }

  @Override
  public VisitorResult visitDomainStatement(QueryLanguageParser.DomainStatementContext ctx) {
    return visitDomainSteps(ctx.domainSteps());
  }

  @Override
  public VisitorResult visitDemainStep(QueryLanguageParser.DemainStepContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    String name = ctx.NAME().getText();
    var filterVisitor =
        new ExpressionVisitor(table(this.stepLogicalTables.get(name).getTable()));
    Optional<BooleanExpression> filter = ctx.domainStepFilter() == null
        ? Optional.empty()
        : Optional.of(filterVisitor.visit(ctx.domainStepFilter()).asCondition());
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
