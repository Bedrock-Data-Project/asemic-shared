package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import java.util.List;
import java.util.Map;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {
  private final Map<String, ActionLogicalTable> stepLogicalTables;

  public SequenceVisitor(Map<String, ActionLogicalTable> stepLogicalTables) {
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
