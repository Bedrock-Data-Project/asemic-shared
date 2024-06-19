package com.asemicanalytics.config.enrichment;

import com.asemicanalytics.core.logicaltable.Enrichment;
import com.asemicanalytics.core.logicaltable.EnrichmentColumnPair;
import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnrichmentResolver {
  public static void resolve(Map<String, ? extends LogicalTable> logicalTables,
                             Optional<EntityLogicalTable> entityLogicalTable,
                             List<EnrichmentDefinition> enrichmentDefinitions) {
    enrichmentDefinitions.forEach(e -> {
      var source = logicalTables.get(e.sourceLogicalTable());
      var target = logicalTables.get(e.targetLogicalTable());
      source.addEnrichment(new Enrichment(target, e.enrichmentColumnPairs()));
    });

    entityLogicalTable.ifPresent(uw -> logicalTables.values().stream()
        .filter(d -> d instanceof ActionLogicalTable)
        .map(d -> (ActionLogicalTable) d)
        .forEach(d -> d.addEnrichment(new Enrichment(uw, List.of(
                new EnrichmentColumnPair(d.getDateColumn().getId(), uw.getDateColumn().getId()),
                new EnrichmentColumnPair(d.entityIdColumn().getId(), uw.entityIdColumn().getId())
            ))
        )));

  }

}
