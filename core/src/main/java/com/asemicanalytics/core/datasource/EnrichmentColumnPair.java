package com.asemicanalytics.core.datasource;

public record EnrichmentColumnPair(
    String sourceColumnId,
    String targetColumnId
) {
}
