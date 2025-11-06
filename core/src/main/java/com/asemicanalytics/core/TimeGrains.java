package com.asemicanalytics.core;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public enum TimeGrains implements TimeGrain {
  min5 {
    @Override
    public long toMinutes() {
      return 5;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMinutes(5);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      return dateTime.truncatedTo(ChronoUnit.HOURS)
          .withMinute((dateTime.getMinute() / 5) * 5);
    }

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
    }
  },
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

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
    }
  },
  min105 {
    @Override
    public long toMinutes() {
      return 105;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMinutes(105);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      int minute = dateTime.getMinute();
      int truncatedMinute = (minute / 105) * 105;
      return dateTime.truncatedTo(ChronoUnit.HOURS).withMinute(truncatedMinute % 60);
    }

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
    }
  },
  min450 {
    @Override
    public long toMinutes() {
      return 450;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMinutes(450);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      int minute = dateTime.getMinute();
      int truncatedMinute = (minute / 450) * 450;
      return dateTime.truncatedTo(ChronoUnit.HOURS).withMinute(truncatedMinute % 60);
    }

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
    }
  },
  min900 {
    @Override
    public long toMinutes() {
      return 900;
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusMinutes(900);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      int minute = dateTime.getMinute();
      int truncatedMinute = (minute / 900) * 900;
      return dateTime.truncatedTo(ChronoUnit.HOURS).withMinute(truncatedMinute % 60);
    }

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
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

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
    }
  },
  hour3 {
    @Override
    public long toMinutes() {
      return TimeUnit.HOURS.toMinutes(3);
    }

    @Override
    public ZonedDateTime next(ZonedDateTime datetime) {
      return datetime.plusHours(3);
    }

    @Override
    public ZonedDateTime truncate(ZonedDateTime dateTime) {
      int hour = dateTime.getHour();
      int truncatedHour = (hour / 3) * 3;
      return dateTime.truncatedTo(ChronoUnit.DAYS).withHour(truncatedHour);
    }

    @Override
    public DataType dataType() {
      return DataType.DATETIME;
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

    @Override
    public DataType dataType() {
      return DataType.DATE;
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

    @Override
    public DataType dataType() {
      return DataType.DATE;
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

    @Override
    public DataType dataType() {
      return DataType.DATE;
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

    @Override
    public DataType dataType() {
      return DataType.DATE;
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

    @Override
    public DataType dataType() {
      return DataType.DATE;
    }
  }
}
