package com.asemicanalytics.config.configloader;

import com.asemicanalytics.core.kpi.Kpi;

public record KpiReference(
    String datasourceId,
    Kpi kpi
) {
}
