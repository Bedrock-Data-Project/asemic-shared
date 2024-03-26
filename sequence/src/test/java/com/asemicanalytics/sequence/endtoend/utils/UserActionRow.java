package com.asemicanalytics.sequence.endtoend.utils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public record UserActionRow(long userId, Duration timestamp) {
  public String toSelectSql(boolean isHeader) {
    String placeholder = isHeader
        ? "SELECT %s AS \"user_id\", %s AS \"ts\", %s as \"date_\""
        : "SELECT %s, %s, %s";
    ZonedDateTime timestamp = DatabaseHelper.BASE_DATE.plus(this.timestamp);
    ZonedDateTime date = timestamp.truncatedTo(ChronoUnit.DAYS);

    return String.format(placeholder, userId,
        String.format("TIMESTAMP WITH TIME ZONE '%s'",
            timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
        String.format("TIMESTAMP '%s'",
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    );
  }
}
