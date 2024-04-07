package com.asemicanalytics.core.kpi;

import java.util.Map;

public record KpixaxisConfig(
    String formula,
    String totalFunction,
    Map<String, KpiComponent> components
) {
}
