package com.asemicanalytics.core;

import java.util.List;

public record SqlResultRow(List<? extends Object> columns) {
}
