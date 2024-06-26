package com.asemicanalytics.config.mapper.dtomapper;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.logicaltable.EnrichmentColumnPair;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EnrichmentDto;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnrichmentDtoMapper implements Function<EnrichmentDto, EnrichmentDefinition> {
  private final String sourceLogicalTableId;

  public EnrichmentDtoMapper(String sourceLogicalTableId) {
    this.sourceLogicalTableId = sourceLogicalTableId;
  }

  @Override
  public EnrichmentDefinition apply(EnrichmentDto dto) {
    var columnPairs = dto.getOn().stream()
        .map(e -> new EnrichmentColumnPair(e.getSource(), e.getTarget()))
        .collect(Collectors.toList());
    return new EnrichmentDefinition(sourceLogicalTableId, dto.getLogicalTable(), columnPairs);
  }
}
