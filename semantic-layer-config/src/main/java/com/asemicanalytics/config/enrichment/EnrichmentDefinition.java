package com.asemicanalytics.config.enrichment;

import com.asemicanalytics.core.logicaltable.EnrichmentColumnPair;
import java.util.List;

public record EnrichmentDefinition(String sourceLogicalTable, String targetLogicalTable,
                                   List<EnrichmentColumnPair> enrichmentColumnPairs) {
}
