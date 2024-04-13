package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import java.util.List;
import java.util.Map;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {
  private final Map<String, ColumnSource> stepColumnSources;

  public SequenceVisitor(Map<String, ColumnSource> stepColumnSources) {
    this.stepColumnSources = stepColumnSources;
  }

  @Override
  public VisitorResult visitStatement(QueryLanguageParser.StatementContext ctx) {
    var result = new VisitorResult(List.of(), Map.of());
    if (ctx.domainStatement() != null) {
      result.merge(new DomainVisitor(stepColumnSources).visit(ctx.domainStatement()));
    }
    result.merge(new MatchVisitor().visit(ctx.matchStatement()));
    return result;
  }
}
