package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class VisitorResult {
  private final List<Step> steps;
  private final Map<String, DomainStep> domain;

  public VisitorResult(List<Step> steps, Map<String, DomainStep> domain) {
    this.steps = new ArrayList<>(steps);
    this.domain = new TreeMap<>(domain);
  }

  public void merge(VisitorResult toMerge) {
    this.steps.addAll(toMerge.steps);

    for (var domainStep : toMerge.domain.values()) {
      String domainStepName = domainStep.name();
      if (this.domain.containsKey(domainStepName)) {
        throw new IllegalArgumentException("Domain step " + domainStepName + " is repeated");
      }
      this.domain.put(domainStepName, domainStep);
    }
  }

  public List<Step> getSteps() {
    return steps;
  }

  public Map<String, DomainStep> getDomain() {
    return domain;
  }
}
