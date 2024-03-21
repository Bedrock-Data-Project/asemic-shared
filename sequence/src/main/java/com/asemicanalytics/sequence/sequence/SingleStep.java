package com.asemicanalytics.sequence.sequence;

import java.util.List;
import java.util.Objects;

public class SingleStep implements Step {
  private final String name;
  private final StepRepetition repetition;

  private final int index;

  public SingleStep(String name, StepRepetition repetition, int index) {
    this.name = name;
    this.repetition = repetition;
    this.index = index;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + repetition;
  }

  @Override
  public List<String> getStepNames() {
    return List.of(name);
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public boolean containsExactRepetition(String stepName) {
    return name.equals(stepName) && repetition.isExactly();
  }

  public StepRepetition getRepetition() {
    return repetition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SingleStep that = (SingleStep) o;
    return index == that.index && Objects.equals(name, that.name)
        && Objects.equals(repetition, that.repetition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, repetition, index);
  }

}
