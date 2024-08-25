package com.asemicanalytics.core;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DatetimeInterval(
    ZonedDateTime from,
    ZonedDateTime to
) {
  public static DatetimeInterval ofMidnight(LocalDate from, LocalDate to) {
    return new DatetimeInterval(
        from.atStartOfDay(ZoneId.of("UTC")),
        to.atStartOfDay(ZoneId.of("UTC")));
  }

  public long days() {
    return ChronoUnit.DAYS.between(from, to) + 1;
  }

  public List<ZonedDateTime> generateAll(TimeGrain timeGrain) {
    var dates = new ArrayList<ZonedDateTime>();
    ZonedDateTime current = timeGrain.truncate(from);
    while (current.compareTo(to) < 1) {
      dates.add(current);
      current = timeGrain.next(current);
    }
    return dates;
  }

  public Optional<DatetimeInterval> intersection(DatetimeInterval interval) {
    var start = from.compareTo(interval.from) < 0 ? interval.from : from;
    var end = to.compareTo(interval.to) < 0 ? to : interval.to;

    if (start.compareTo(end) > 0) {
      return Optional.empty();
    }
    return Optional.of(new DatetimeInterval(start, end));
  }

  public boolean contains(ZonedDateTime date) {
    return from.compareTo(date) <= 0 && to.compareTo(date) >= 0;
  }

  public DatetimeInterval plusDays(long days) {
    return new DatetimeInterval(from.plusDays(days), to.plusDays(days));
  }

  public DatetimeInterval plusDays(long fromDays, long toDays) {
    return new DatetimeInterval(from.plusDays(fromDays), to.plusDays(toDays));
  }

  public DateInterval toDateInterval() {
    return new DateInterval(from.toLocalDate(), to.toLocalDate());
  }
}
