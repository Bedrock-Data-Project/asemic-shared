package com.asemicanalytics.sequence.sequence;

import com.asemicanalytics.core.DatetimeInterval;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

public class Sequence {
  private final List<Step> steps;
  private final Set<String> domain;
  private final DatetimeInterval datetimeInterval;
  private final Duration timeHorizon;
  private final boolean ignoreIncompleteSequences;
  private final Map<String, StepTable> stepsRepository;

  public Sequence(List<Step> steps, Set<String> domain, DatetimeInterval datetimeInterval,
                  Duration timeHorizon, boolean ignoreIncompleteSequences,
                  Map<String, StepTable> stepsRepository) {
    this.steps = steps;
    this.domain = domain;
    this.datetimeInterval = datetimeInterval;
    this.timeHorizon = timeHorizon;
    this.ignoreIncompleteSequences = ignoreIncompleteSequences;
    this.stepsRepository = stepsRepository;
    validate();
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(" >> ");
    steps.forEach(step -> joiner.add(step.toString()));
    return joiner.toString();
  }

  public Set<String> getDomainActions() {
    SortedSet<String> wholeDomain = new TreeSet<>(domain);
    steps.forEach(step -> wholeDomain.addAll(step.getStepNames()));
    return wholeDomain;
  }

  public StepTable getStepTable(String stepName) {
    return stepsRepository.get(stepName);
  }

  public DatetimeInterval getDatetimeInterval() {
    return datetimeInterval;
  }

  public boolean isStartStepRepeated() {
    List<String> startSteps = steps.get(0).getStepNames();

    return steps.stream().skip(1)
        .anyMatch(s -> s.getStepNames().stream().anyMatch(startSteps::contains));
  }

  public List<Step> getSteps() {
    return steps;
  }

  public Duration getTimeHorizon() {
    return timeHorizon;
  }

  private void validate() {
    if (steps.isEmpty()) {
      throw new IllegalArgumentException("Sequence must contain at least one step");
    }

    if (steps.size() > 1
        && steps.get(0).getStepNames().stream().anyMatch(steps.get(1).getStepNames()::contains)) {
      throw new IllegalArgumentException("First step cannot be repeated");
    }

    for (int i = 1; i < steps.size(); i++) {
      for (String stepName : steps.get(i).getStepNames()) {
        if (steps.get(i - 1).getStepNames().contains(stepName)
            && !steps.get(i - 1).containsExactRepetition(stepName)) {
          throw new IllegalArgumentException(
              "Only last repeated step in sequence can have non exact repetitions!");
        }
      }
    }

  }
}
