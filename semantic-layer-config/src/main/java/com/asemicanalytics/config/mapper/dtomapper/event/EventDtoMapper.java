package com.asemicanalytics.config.mapper.dtomapper.event;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.mapper.dtomapper.EnrichmentDtoMapper;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EventLogicalTableDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;

public class EventDtoMapper
    implements Function<EventLogicalTableDto, EventLogicalTable> {
  private final String id;
  private final String appId;
  private final List<EnrichmentDefinition> enrichmentCollector;

  public EventDtoMapper(String logicalTableId, String appId,
                        List<EnrichmentDefinition> enrichmentCollector) {
    this.id = logicalTableId;
    this.appId = appId;
    this.enrichmentCollector = enrichmentCollector;
  }

  @Override
  public EventLogicalTable apply(EventLogicalTableDto dto) {
    dto.getEnrichments().ifPresent(enrichments -> enrichments.forEach(e -> enrichmentCollector.add(
        new EnrichmentDtoMapper(id).apply(e))));

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    for (var entry : dto.getColumns().getAdditionalProperties().entrySet()) {
      var id = entry.getKey();
      var column = entry.getValue();
      columns.put(id, new EventColumnDtoMapper(id).apply(column));
    }
    var tags = dto.getTags().map(Set::copyOf).orElse(Set.of());

    return new EventLogicalTable(
        id,
        DefaultLabel.of(dto.getLabel(), id),
        dto.getDescription(),
        TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
        new Columns<>(columns),
        Map.of(),
        dto.getWhere(),
        tags
    );

  }
}
