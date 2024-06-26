package com.asemicanalytics.config.mapper;

import com.asemicanalytics.core.kpi.Kpi;

public record KpiReference(
    String logicalTableId,
    Kpi kpi
) {
}
