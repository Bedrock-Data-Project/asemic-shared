package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.TemporalDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomDailyDatasourceDtoMapper
    implements Function<CustomDailyDatasourceDto, TemporalDatasource> {
  private final String datasourceId;
  private final String appId;
  private final List<EnrichmentDefinition> enrichmentCollector;

  public CustomDailyDatasourceDtoMapper(String datasourceId, String appId,
                                        List<EnrichmentDefinition> enrichmentCollector) {
    this.datasourceId = datasourceId;
    this.appId = appId;
    this.enrichmentCollector = enrichmentCollector;
  }

  @Override
  public TemporalDatasource apply(CustomDailyDatasourceDto dto) {
    dto.getEnrichments().ifPresent(enrichments -> enrichments.forEach(e -> enrichmentCollector.add(
        new EnrichmentDtoMapper(datasourceId).apply(e))));

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    dto.getColumns().stream().map(new ColumnDtoMapper())
        .forEach(c -> columns.put(c.getId(), c));
    dto.getComputedColumns().ifPresent(cols ->
        cols.stream()
            .map(new ColumnComputedDtoMapper())
            .forEach(c -> {
              if (columns.put(c.getId(), c) != null) {
                throw new IllegalArgumentException(
                    "Duplicate column id: " + c.getId() + " in datasource: " + datasourceId);
              }
            }));

    var kpiMap = dto.getKpis().map(k -> k.stream().collect(Collectors.toMap(KpiDto::getId, d -> d)))
        .orElse(Map.of());
    return new TemporalDatasource(
        datasourceId,
        DefaultLabel.of(dto.getLabel(), datasourceId),
        dto.getDescription(),
        TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
        new Columns(columns),
        kpiMap.entrySet().stream().collect(Collectors.toMap(
            e -> e.getKey(),
            e -> new KpiDtoMapper(appId, dto.getDateColumn(), dto.getKpis().orElse(List.of()),
                dto.getTableName()).apply(e.getValue()))),
        TimeGrains.day,
        Set.of()
    );
  }
}
