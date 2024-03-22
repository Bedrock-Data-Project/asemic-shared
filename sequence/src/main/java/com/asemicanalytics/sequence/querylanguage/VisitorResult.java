package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class VisitorResult {
  private final List<Step> steps;
  private final TreeSet<String> domain;

  public VisitorResult(List<Step> steps, Set<String> domain) {
    this.steps = new ArrayList<>(steps);
    this.domain = new TreeSet<>(domain);
  }

  public void merge(VisitorResult toMerge) {
    this.steps.addAll(toMerge.steps);
    this.domain.addAll(toMerge.domain);
  }

  public List<Step> getSteps() {
    return steps;
  }

  public TreeSet<String> getDomain() {
    return domain;
  }
}
