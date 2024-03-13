package com.asemicanalytics.sequence.sequence;

public class SingleStep implements Step {
  private final String name;

  public SingleStep(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
