package com.asemicanalytics.core.logicaltable;

import java.util.List;

public record Enrichment(
    LogicalTable<?> targetLogicalTable,
    List<EnrichmentColumnPair> enrichmentColumnPairs
) {
  public Enrichment {
    if (enrichmentColumnPairs.isEmpty()) {
      throw new IllegalArgumentException("Must have at least one column pair");
    }
  }
}
