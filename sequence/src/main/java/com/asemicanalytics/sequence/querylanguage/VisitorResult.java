package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.Step;
import java.util.ArrayList;
import java.util.List;

class VisitorResult {
  private final List<Step> steps;

  public VisitorResult(List<Step> steps) {
    this.steps = new ArrayList<>(steps);
  }

  public void merge(VisitorResult toMerge) {
    this.steps.addAll(toMerge.steps);
  }

  public List<Step> getSteps() {
    return steps;
  }
}
