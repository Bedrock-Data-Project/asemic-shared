package com.asemicanalytics.core;

public record RelativeDaysInterval(
    int from,
    int to
) {

  public RelativeDaysInterval {

    if (from > to) {
      throw new IllegalArgumentException(
          "relative days from must be less than or equal to relative days to");
    }
  }

  public long days() {
    return to - from + 1;
  }

}
