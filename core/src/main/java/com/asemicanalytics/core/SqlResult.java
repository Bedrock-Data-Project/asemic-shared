package com.asemicanalytics.core;

import java.time.Duration;
import java.util.List;

public record SqlResult(List<SqlResultRow> rows, String sql, Duration duration) {
}
