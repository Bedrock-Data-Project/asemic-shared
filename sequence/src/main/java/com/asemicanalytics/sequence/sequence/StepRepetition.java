package com.asemicanalytics.sequence.sequence;

import java.util.Optional;

public record StepRepetition(int from, Optional<Integer> to) {

  public static StepRepetition oneOrMore() {
    return new StepRepetition(1, Optional.empty());
  }

  public static StepRepetition exactly(int times) {
    return new StepRepetition(times, Optional.of(times));
  }

  public static StepRepetition atLeast(int times) {
    return new StepRepetition(times, Optional.empty());
  }

  public static StepRepetition between(int from, int to) {
    return new StepRepetition(from, Optional.of(to));
  }
}
