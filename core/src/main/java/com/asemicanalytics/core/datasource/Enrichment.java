package com.asemicanalytics.core.datasource;

import java.util.List;

public record Enrichment(
    Datasource targetDatasource,
    List<EnrichmentColumnPair> enrichmentColumnPairs
) {
  public Enrichment {
    if (enrichmentColumnPairs.isEmpty()) {
      throw new IllegalArgumentException("Must have at least one column pair");
    }
  }
}
