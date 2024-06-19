package com.asemicanalytics.config.enrichment;

import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.core.datasource.Enrichment;
import com.asemicanalytics.core.datasource.EnrichmentColumnPair;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnrichmentResolver {
  public static void resolve(Map<String, Datasource> datasources,
                             Optional<UserWideDatasource> userWideDatasource,
                             List<EnrichmentDefinition> enrichmentDefinitions) {
    enrichmentDefinitions.forEach(e -> {
      var source = datasources.get(e.sourceDatasourceId());
      var target = datasources.get(e.targetDatasourceId());
      source.addEnrichment(new Enrichment(target, e.enrichmentColumnPairs()));
    });

    userWideDatasource.ifPresent(uw -> datasources.values().stream()
        .filter(d -> d instanceof UserActionDatasource)
        .map(d -> (UserActionDatasource) d)
        .forEach(d -> d.addEnrichment(new Enrichment(uw, List.of(
                new EnrichmentColumnPair(d.getDateColumn().getId(), uw.getDateColumn().getId()),
                new EnrichmentColumnPair(d.getUserIdColumn().getId(), uw.getUserIdColumn().getId())
            ))
        )));

  }

}
