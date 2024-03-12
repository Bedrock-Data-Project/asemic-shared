package com.asemicanalytics.core;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public enum TimeGrains implements TimeGrain {
  min15 {
    @Override
    public long toMinutes() {
      return 15;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMinutes(15);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return dateTime.truncatedTo(ChronoUnit.HOURS)
          .withMinute((dateTime.getMinute() / 15) * 15);
    }
  },
  hour {
    @Override
    public long toMinutes() {
      return TimeUnit.HOURS.toMinutes(1);
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusHours(1);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return dateTime.truncatedTo(ChronoUnit.HOURS);
    }
  },
  day {
    @Override
    public long toMinutes() {
      return TimeUnit.DAYS.toMinutes(1);
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusDays(1);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return dateTime.truncatedTo(ChronoUnit.DAYS);
    }
  },
  week {
    @Override
    public long toMinutes() {
      return day.toMinutes() * 7;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusDays(7);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return day.truncate(dateTime.with(DayOfWeek.MONDAY));
    }
  },
  month {
    @Override
    public long toMinutes() {
      return day.toMinutes() * 30;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMonths(1);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return day.truncate(dateTime.withDayOfMonth(1));
    }
  },
  quarter {
    @Override
    public long toMinutes() {
      return month.toMinutes() * 3;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMonths(3);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      int quarter = (dateTime.getMonthValue() - 1) / 3 + 1;
      return month.truncate(dateTime.withMonth((quarter - 1) * 3 + 1));
    }
  },
  year {
    @Override
    public long toMinutes() {
      return month.toMinutes() * 12;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusYears(1);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return month.truncate(dateTime.withMonth(1));
    }
  }
}
