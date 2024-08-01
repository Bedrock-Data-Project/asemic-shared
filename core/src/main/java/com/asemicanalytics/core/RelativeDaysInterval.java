package com.asemicanalytics.core;

public record RelativeDaysInterval(
    int from,
    int to
) {

  public RelativeDaysInterval {

    if (from > 0 || to > 0) {
      throw new IllegalArgumentException("window days must be 0 or negative");
    }

    if (from > to) {
      throw new IllegalArgumentException(
          "relative days from must be less than or equal to relative days to");
    }
  }

  public long days() {
    return to - from + 1;
  }

}
