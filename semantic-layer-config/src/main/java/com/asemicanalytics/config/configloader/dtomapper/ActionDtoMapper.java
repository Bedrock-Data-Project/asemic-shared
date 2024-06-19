package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.PaymentTransactionActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;

public class ActionDtoMapper
    implements Function<ActionLogicalTableDto, ActionLogicalTable> {
  private final String id;
  private final String appId;
  private final List<EnrichmentDefinition> enrichmentCollector;

  public ActionDtoMapper(String logicalTableId, String appId,
                         List<EnrichmentDefinition> enrichmentCollector) {
    this.id = logicalTableId;
    this.appId = appId;
    this.enrichmentCollector = enrichmentCollector;
  }

  @Override
  public ActionLogicalTable apply(ActionLogicalTableDto dto) {
    dto.getEnrichments().ifPresent(enrichments -> enrichments.forEach(e -> enrichmentCollector.add(
        new EnrichmentDtoMapper(id).apply(e))));

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    dto.getColumns().stream().map(new ColumnDtoMapper())
        .forEach(c -> columns.put(c.getId(), c));
    dto.getComputedColumns().ifPresent(cols ->
        cols.stream()
            .map(new ColumnComputedDtoMapper())
            .forEach(c -> {
              if (columns.put(c.getId(), c) != null) {
                throw new IllegalArgumentException(
                    "Duplicate column id: " + c.getId() + " in logicalTable: " + id);
              }
            }));

    var tags = dto.getTags().map(Set::copyOf).orElse(Set.of());

    if (tags.contains(FirstAppearanceActionLogicalTable.TAG)) {
      return new FirstAppearanceActionLogicalTable(
          id,
          DefaultLabel.of(dto.getLabel(), id),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          Map.of(),
          tags
      );
    } else if (tags.contains(ActivityLogicalTable.TAG)) {
      return new ActivityLogicalTable(
          id,
          DefaultLabel.of(dto.getLabel(), id),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          Map.of(),
          tags
      );
    } else if (tags.contains(PaymentTransactionActionLogicalTable.TAG)) {
      return new PaymentTransactionActionLogicalTable(
          id,
          DefaultLabel.of(dto.getLabel(), id),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          Map.of(),
          tags
      );
    } else {
      return new ActionLogicalTable(
          id,
          DefaultLabel.of(dto.getLabel(), id),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          Map.of(),
          tags
      );
    }
  }
}
