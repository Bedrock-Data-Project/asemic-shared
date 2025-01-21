package com.asemicanalytics.sequence.sequence;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Step {
  List<String> getStepNames();

  default List<String> getRealStepNames(Collection<DomainStep> domainSteps) {
    return getStepNames().stream().map(stepName -> {
      for (DomainStep domainStep : domainSteps) {
        if (domainStep.alias().orElse("").equals(stepName)) {
          return domainStep.actionLogicalTableName();
        }
      }
      return stepName;
    }).collect(Collectors.toList());
  }

  int getIndex();

  boolean containsExactRepetition(String stepName);
}
