package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import java.util.List;
import java.util.Set;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {

  private int currentIndex = 1;

  @Override
  public VisitorResult visitStatement(QueryLanguageParser.StatementContext ctx) {
    var result = new VisitorResult(List.of(), Set.of());
    if (ctx.domainStatement() != null) {
      result.merge(visitDomainStatement(ctx.domainStatement()));
    }
    result.merge(visitMatchStatement(ctx.matchStatement()));
    return result;
  }

  @Override
  public VisitorResult visitDomainStatement(QueryLanguageParser.DomainStatementContext ctx) {
    return visitDomainSteps(ctx.domainSteps());
  }

  @Override
  public VisitorResult visitDomainSteps(QueryLanguageParser.DomainStepsContext ctx) {
    var result = new VisitorResult(List.of(), Set.of());
    for (var domain : ctx.NAME()) {
      result.getDomain().add(domain.getText());
    }
    return result;
  }

  @Override
  public VisitorResult visitMatchStatement(QueryLanguageParser.MatchStatementContext ctx) {
    VisitorResult root = visitStep(ctx.step());
    for (QueryLanguageParser.ChainedStepContext cs : ctx.chainedStep()) {
      VisitorResult result = visitChainedStep(cs);
      root.merge(result);
    }
    return root;
  }

  @Override
  public VisitorResult visitSingleStep(QueryLanguageParser.SingleStepContext ctx) {
    final StepRepetition repetition = getStepRepetition(ctx);
    return new VisitorResult(
        List.of(new SingleStep(ctx.NAME().getText(), repetition, currentIndex)), Set.of());
  }

  private StepRepetition getStepRepetition(QueryLanguageParser.SingleStepContext ctx) {
    final StepRepetition repetition;
    if (ctx.range() != null) {
      if (ctx.range().to != null) {
        repetition = StepRepetition.between(
            Integer.parseInt(ctx.range().from.getText()),
            Integer.parseInt(ctx.range().to.getText()));
      } else {
        repetition = StepRepetition.atLeast(Integer.parseInt(ctx.range().from.getText()));
      }
    } else {
      repetition = StepRepetition.atLeast(1);
    }
    return repetition;
  }


  @Override
  public VisitorResult visitGroupStep(QueryLanguageParser.GroupStepContext ctx) {
    List<SingleStep> singleSteps = ctx.singleStep().stream()
        .map(this::visitSingleStep)
        .map(v -> (SingleStep) v.getSteps().get(0))
        .toList();
    return new VisitorResult(List.of(new GroupStep(singleSteps)), Set.of());
  }

  @Override
  public VisitorResult visitChainedStep(QueryLanguageParser.ChainedStepContext ctx) {
    currentIndex++;
    return visitStep(ctx.step());
  }
}
