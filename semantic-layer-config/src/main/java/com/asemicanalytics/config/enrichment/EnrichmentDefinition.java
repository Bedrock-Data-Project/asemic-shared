package com.asemicanalytics.config.enrichment;

import com.asemicanalytics.core.datasource.EnrichmentColumnPair;
import java.util.List;

public record EnrichmentDefinition(String sourceDatasourceId, String targetDatasourceId,
                                   List<EnrichmentColumnPair> enrichmentColumnPairs) {
}
