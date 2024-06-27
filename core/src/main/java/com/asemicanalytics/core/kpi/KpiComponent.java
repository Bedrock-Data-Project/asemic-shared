package com.asemicanalytics.core.kpi;

import java.util.TreeSet;

public record KpiComponent(
    String select,
    TreeSet<String> filters
) {
}
