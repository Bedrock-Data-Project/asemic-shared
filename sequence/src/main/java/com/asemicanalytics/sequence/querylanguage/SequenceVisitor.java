package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import java.util.List;
import java.util.Map;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {
  private final EventLogicalTables stepLogicalTables;

  public SequenceVisitor(EventLogicalTables stepLogicalTables) {
    this.stepLogicalTables = stepLogicalTables;
  }

  @Override
  public VisitorResult visitStatement(QueryLanguageParser.StatementContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    if (ctx.domainStatement() != null) {
      result.merge(new DomainVisitor(stepLogicalTables).visit(ctx.domainStatement()));
    }
    result.merge(new MatchVisitor().visit(ctx.matchStatement()));
    return result;
  }
}
