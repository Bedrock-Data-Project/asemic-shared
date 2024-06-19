package com.asemicanalytics.config.configloader;

import com.asemicanalytics.core.kpi.Kpi;

public record KpiReference(
    String logicalTableId,
    Kpi kpi
) {
}
