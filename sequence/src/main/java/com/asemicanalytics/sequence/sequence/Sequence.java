package com.asemicanalytics.sequence.sequence;

import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.StringJoiner;

public class Sequence {
  private final List<Step> steps;
  private final Map<String, DomainStep> domain;
  private final Duration timeHorizon;
  private final boolean ignoreIncompleteSequences;
  private final EventLogicalTables stepTables;

  public Sequence(List<Step> steps, Map<String, DomainStep> domain,
                  Duration timeHorizon, boolean ignoreIncompleteSequences,
                  EventLogicalTables stepTables) {
    this.steps = steps;
    this.domain = domain;
    this.timeHorizon = timeHorizon;
    this.ignoreIncompleteSequences = ignoreIncompleteSequences;
    this.stepTables = stepTables;
    validate();
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(" >> ");
    steps.forEach(step -> joiner.add(step.toString()));
    return joiner.toString();
  }

  public DomainStep getDomainForStep(String stepName) {
    return domain.getOrDefault(stepName,
        new DomainStep(stepName, Optional.empty(), Optional.empty()));
  }

  public List<DomainStep> getDomainActions() {
    Map<String, DomainStep> resolvedSteps = new HashMap<>();
    SequencedMap<String, DomainStep> stepsOrdered = new LinkedHashMap<>();

    for (var domainStep : domain.values()) {
      String domainStepName = domainStep.name();
      if (!resolvedSteps.containsKey(domainStepName)) {
        resolvedSteps.put(domainStepName, domainStep);
      } else {
        throw new IllegalArgumentException("Domain step " + domainStepName + " is repeated");
      }
    }

    for (Step step : steps) {
      for (String action : step.getStepNames()) {
        if (!resolvedSteps.containsKey(action)) {
          var domainStep = new DomainStep(action, Optional.empty(), Optional.empty());
          resolvedSteps.put(action, domainStep);
          stepsOrdered.put(action, domainStep);
        } else {
          stepsOrdered.put(action, resolvedSteps.get(action));
        }
      }
    }

    // add remaining steps
    for (var entry : resolvedSteps.entrySet()) {
      String stepName = entry.getKey();
      if (!stepsOrdered.containsKey(stepName)) {
        stepsOrdered.put(stepName, entry.getValue());
      }
    }

    return stepsOrdered.values().stream().toList();
  }

  public EventLogicalTable getTable(String stepName) {
    return stepTables.get(stepName);
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
