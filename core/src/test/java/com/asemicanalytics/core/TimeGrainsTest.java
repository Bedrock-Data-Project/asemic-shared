package com.asemicanalytics.core;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;


class TimeGrainTest {
  @Test
  void min15Truncate() {
    assertEquals(
        TimeGrains.min15.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.min15.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.min15.truncate(ZonedDateTime.of(2023, 3, 10, 3, 16, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 15, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void hourTruncate() {
    assertEquals(
        TimeGrains.hour.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.hour.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.hour.truncate(ZonedDateTime.of(2023, 3, 10, 3, 16, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void dayTruncate() {
    assertEquals(
        TimeGrains.day.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.day.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.day.truncate(ZonedDateTime.of(2023, 3, 10, 3, 16, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void weekTruncate() {
    assertEquals(
        TimeGrains.week.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 6, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.week.truncate(ZonedDateTime.of(2023, 3, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 6, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.week.truncate(ZonedDateTime.of(2023, 3, 11, 3, 16, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 6, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void monthTruncate() {
    assertEquals(
        TimeGrains.month.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.month.truncate(ZonedDateTime.of(2023, 3, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void quarterTruncate() {
    assertEquals(
        TimeGrains.quarter.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.quarter.truncate(ZonedDateTime.of(2023, 3, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.quarter.truncate(ZonedDateTime.of(2023, 5, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.quarter.truncate(ZonedDateTime.of(2023, 12, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 10, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void yearTruncate() {
    assertEquals(
        TimeGrains.year.truncate(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.year.truncate(ZonedDateTime.of(2023, 3, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.year.truncate(ZonedDateTime.of(2023, 5, 11, 3, 0, 5, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void min15Next() {
    assertEquals(
        TimeGrains.min15.next(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 3, 15, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.min15.next(ZonedDateTime.of(2023, 3, 10, 3, 45, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 4, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void hourNext() {
    assertEquals(
        TimeGrains.hour.next(ZonedDateTime.of(2023, 3, 10, 3, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 10, 4, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.hour.next(ZonedDateTime.of(2023, 3, 10, 23, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 11, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void dayNext() {
    assertEquals(
        TimeGrains.day.next(ZonedDateTime.of(2023, 3, 10, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 3, 11, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.day.next(ZonedDateTime.of(2023, 3, 31, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void weekNext() {
    assertEquals(
        TimeGrains.week.next(ZonedDateTime.of(2023, 3, 28, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 4, 4, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void monthNext() {
    assertEquals(
        TimeGrains.month.next(ZonedDateTime.of(2023, 12, 1, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void quarterNext() {
    assertEquals(
        TimeGrains.quarter.next(ZonedDateTime.of(2023, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2023, 7, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
    assertEquals(
        TimeGrains.quarter.next(ZonedDateTime.of(2023, 10, 1, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }

  @Test
  void yearNext() {
    assertEquals(
        TimeGrains.year.next(ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))),
        ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
    );
  }
}
