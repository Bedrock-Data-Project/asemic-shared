package com.asemicanalytics.core;

import java.time.ZonedDateTime;

public interface TimeGrain {
  long toMinutes();

  ZonedDateTime next(ZonedDateTime datetime);

  ZonedDateTime truncate(ZonedDateTime dateTime);
}
