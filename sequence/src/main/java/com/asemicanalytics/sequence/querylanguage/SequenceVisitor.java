package com.asemicanalytics.sequence.querylanguage;

import java.util.List;
import java.util.Map;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {
  @Override
  public VisitorResult visitStatement(QueryLanguageParser.StatementContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    if (ctx.domainStatement() != null) {
      result.merge(new DomainVisitor().visit(ctx.domainStatement()));
    }
    result.merge(new MatchVisitor().visit(ctx.matchStatement()));
    return result;
  }
}
