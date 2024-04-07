package com.asemicanalytics.core.kpi;

import com.asemicanalytics.core.TableReference;
import java.util.Optional;

public record KpiComponent(
    String select,
    Optional<String> where,
    TableReference table
) {
}
