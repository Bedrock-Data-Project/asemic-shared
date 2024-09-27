package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import java.util.List;
import java.util.Map;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {
  private final Map<String, EventLogicalTable> stepLogicalTables;

  public SequenceVisitor(Map<String, EventLogicalTable> stepLogicalTables) {
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
