package com.asemicanalytics.core;

import java.util.List;

public record SqlResult(List<SqlResultRow> rows) {
}
