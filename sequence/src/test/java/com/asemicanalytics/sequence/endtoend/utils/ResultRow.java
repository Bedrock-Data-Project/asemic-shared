package com.asemicanalytics.sequence.endtoend.utils;

import com.asemicanalytics.core.SqlResultRow;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public record ResultRow(
    long userId, Duration timestamp, String action, long sequence, long subsequence,
    long repetitions, long repetition, Long step, boolean isValid) {
  public SqlResultRow toSqlResultRow() {
    return new SqlResultRow(
        Arrays.asList(userId, DatabaseHelper.BASE_DATE.plus(timestamp),
            DatabaseHelper.BASE_DATE.plus(timestamp).truncatedTo(ChronoUnit.DAYS),
            action, sequence, subsequence, repetitions, repetition, step, isValid));
  }
}
