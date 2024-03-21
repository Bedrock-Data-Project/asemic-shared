package com.asemicanalytics.sequence.sequence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.checkerframework.checker.units.qual.A;

public class GroupStep implements Step {
  private final List<SingleStep> steps;

  public GroupStep(List<SingleStep> steps) {
    this.steps = steps;
    Set<String> distinctSteps = new HashSet<>();
    for (SingleStep step : steps) {
      if (!distinctSteps.add(step.getName())) {
        throw new IllegalArgumentException("Step " + step.getName() + " is repeated in the group");
      }
    }
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

  @Override
  public boolean containsExactRepetition(String stepName) {
    return steps.stream().anyMatch(step -> step.containsExactRepetition(stepName));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupStep groupStep = (GroupStep) o;
    return Objects.equals(steps, groupStep.steps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(steps);
  }
}
