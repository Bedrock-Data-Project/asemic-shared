package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.TemporalDatasource;
import com.asemicanalytics.core.datasource.useraction.ActivityUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.PaymentTransactionUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.RegistrationUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserActionDatasourceDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserActionDatasourceDtoMapper
    implements Function<UserActionDatasourceDto, UserActionDatasource> {
  private final String datasourceId;
  private final String appId;
  private final List<EnrichmentDefinition> enrichmentCollector;

  public UserActionDatasourceDtoMapper(String datasourceId, String appId,
                                       List<EnrichmentDefinition> enrichmentCollector) {
    this.datasourceId = datasourceId;
    this.appId = appId;
    this.enrichmentCollector = enrichmentCollector;
  }

  @Override
  public UserActionDatasource apply(UserActionDatasourceDto dto) {
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

    var tags = dto.getTags().map(Set::copyOf).orElse(Set.of());
    var dateColumnId = dto.getColumns().stream()
        .filter(c -> c.getTags()
            .map(t -> t.contains(TemporalDatasource.DATE_COLUMN_TAG))
            .orElse(false))
        .findFirst().orElseThrow(() -> new IllegalArgumentException(
            "No date column found in datasource: " + datasourceId)).getId();


    if (tags.contains(RegistrationUserActionDatasource.DATASOURCE_TAG)) {
      return new RegistrationUserActionDatasource(
          datasourceId,
          DefaultLabel.of(dto.getLabel(), datasourceId),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          kpiMap.entrySet().stream().collect(Collectors.toMap(
              Map.Entry::getKey, e -> new KpiDtoMapper(
                  appId, dateColumnId,
                  dto.getKpis().orElse(List.of()),
                  dto.getTableName()).apply(e.getValue()))),
          tags
      );
    } else if (tags.contains(ActivityUserActionDatasource.DATASOURCE_TAG)) {
      return new ActivityUserActionDatasource(
          datasourceId,
          DefaultLabel.of(dto.getLabel(), datasourceId),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          kpiMap.entrySet().stream().collect(Collectors.toMap(
              Map.Entry::getKey, e -> new KpiDtoMapper(
                  appId, dateColumnId,
                  dto.getKpis().orElse(List.of()),
                  dto.getTableName()).apply(e.getValue()))),
          tags
      );
    } else if (tags.contains(PaymentTransactionUserActionDatasource.DATASOURCE_TAG)) {
      return new PaymentTransactionUserActionDatasource(
          datasourceId,
          DefaultLabel.of(dto.getLabel(), datasourceId),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          kpiMap.entrySet().stream().collect(Collectors.toMap(
              Map.Entry::getKey, e -> new KpiDtoMapper(
                  appId, dateColumnId,
                  dto.getKpis().orElse(List.of()),
                  dto.getTableName()).apply(e.getValue()))),
          tags
      );
    } else {
      return new UserActionDatasource(
          datasourceId,
          DefaultLabel.of(dto.getLabel(), datasourceId),
          dto.getDescription(),
          TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
          new Columns(columns),
          kpiMap.entrySet().stream().collect(Collectors.toMap(
              Map.Entry::getKey, e -> new KpiDtoMapper(
                  appId, dateColumnId,
                  dto.getKpis().orElse(List.of()),
                  dto.getTableName()).apply(e.getValue()))),
          tags
      );
    }
  }
}
