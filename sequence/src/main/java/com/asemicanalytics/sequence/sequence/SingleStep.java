package com.asemicanalytics.sequence.sequence;

import java.util.List;

public class SingleStep implements Step {
  private final String name;
  private final int index;

  public SingleStep(String name, int index) {
    this.name = name;
    this.index = index;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public List<String> getStepNames() {
    return List.of(name);
  }

  @Override
  public int getIndex() {
    return index;
  }
}
