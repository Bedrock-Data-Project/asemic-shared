package com.asemicanalytics.sequence.sequence;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class GroupStep implements Step {
  private final List<SingleStep> steps;

  public GroupStep(List<SingleStep> steps) {
    this.steps = steps;
  }

  public List<SingleStep> getSteps() {
    return steps;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(", ");
    steps.forEach(step -> joiner.add(step.toString()));
    return "(" + joiner + ")";
  }

  @Override
  public List<String> getStepNames() {
    return steps.stream().map(SingleStep::getName).collect(Collectors.toList());
  }

  @Override
  public int getIndex() {
    return steps.get(0).getIndex();
  }
}
