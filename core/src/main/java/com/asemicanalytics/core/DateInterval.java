package com.asemicanalytics.core;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DateInterval(
    LocalDate from,
    LocalDate to
) {
  public long days() {
    return ChronoUnit.DAYS.between(from, to) + 1;
  }

  public List<LocalDate> generateAll() {
    var dates = new ArrayList<LocalDate>();
    LocalDate current = from;
    while (current.compareTo(to) < 1) {
      dates.add(current);
      current = current.plusDays(1);
    }
    return dates;
  }

  public Optional<DateInterval> intersection(DateInterval interval) {
    var start = from.isBefore(interval.from) ? interval.from : from;
    var end = to.isBefore(interval.to) ? to : interval.to;

    if (start.isAfter(end)) {
      return Optional.empty();
    }
    return Optional.of(new DateInterval(start, end));
  }

  public boolean contains(LocalDate date) {
    return !from.isAfter(date) && !to.isBefore(date);
  }

  public DateInterval plusDays(long days) {
    return new DateInterval(from.plusDays(days), to.plusDays(days));
  }

  public DateInterval plusDays(long fromDays, long toDays) {
    return new DateInterval(from.plusDays(fromDays), to.plusDays(toDays));
  }
}
