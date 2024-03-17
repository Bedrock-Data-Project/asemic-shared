package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import java.util.List;

class SequenceVisitor extends QueryLanguageBaseVisitor<VisitorResult> {

  private int currentIndex = 1;

  @Override
  public VisitorResult visitSequence(QueryLanguageParser.SequenceContext ctx) {
    VisitorResult root = visitStep(ctx.step());
    for (QueryLanguageParser.ChainedStepContext cs : ctx.chainedStep()) {
      VisitorResult result = visitChainedStep(cs);
      root.merge(result);
    }
    return root;
  }

  @Override
  public VisitorResult visitSingleStep(QueryLanguageParser.SingleStepContext ctx) {
    return new VisitorResult(List.of(new SingleStep(ctx.getText(), currentIndex)));
  }

  @Override
  public VisitorResult visitGroupStep(QueryLanguageParser.GroupStepContext ctx) {
    List<SingleStep> singleSteps = ctx.singleStep().stream()
        .map(this::visitSingleStep)
        .map(v -> (SingleStep) v.getSteps().get(0))
        .toList();
    return new VisitorResult(List.of(new GroupStep(singleSteps)));
  }

  @Override
  public VisitorResult visitChainedStep(QueryLanguageParser.ChainedStepContext ctx) {
    var result = visitStep(ctx.step());
    currentIndex++;
    return result;
  }
}
