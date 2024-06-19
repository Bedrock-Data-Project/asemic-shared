package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.configparser.EntityDto;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.FirstAppearanceColumn;
import com.asemicanalytics.core.logicaltable.entity.TotalColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityMapper
    implements Function<EntityDto, EntityLogicalTable> {
  private final String appId;

  public EntityMapper(String appId) {
    this.appId = appId;
  }

  @Override
  public EntityLogicalTable apply(EntityDto dto) {
    var firstAppearanceActionLogicalTable =
        (FirstAppearanceActionLogicalTable) dto.actionLogicalTables()
            .values().stream()
            .filter(d -> d instanceof FirstAppearanceActionLogicalTable)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No logical table tagged with "
                + FirstAppearanceActionLogicalTable.TAG + " found"));

    EntityPropertiesDto mergedColumns = new EntityPropertiesDtoMapper().apply(dto.columns());
    List<EntityKpiDto> mergedKpis = new EntityKpisDtoMapper(
        dto.config(), mergedColumns, firstAppearanceActionLogicalTable.getDateColumnId()).apply(
        dto.kpis());

    var firstAppearanceColumnsMap =
        mergedColumns.getFirstAppearanceProperties().orElse(List.of()).stream()
            .collect(Collectors.toMap(
                c -> c.getColumn().getId(),
                c -> new FirstAppearanceColumn(new ColumnDtoMapper().apply(c.getColumn()),
                    c.getSourceColumn().orElse(c.getColumn().getId())), (a, b) -> a,
                LinkedHashMap::new));

    var actionColumnsMap =
        mergedColumns.getActionProperties().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(), c -> {
              var logicalTable = dto.actionLogicalTables().get(c.getActionLogicalTable());
              if (logicalTable == null) {
                throw new IllegalArgumentException("Action logical table not found: "
                    + c.getActionLogicalTable()
                    + " in entity for column: "
                    + c.getColumn().getId());
              }

              var window = c.getWindow().orElse(List.of(0, 0));
              if (window.isEmpty()) {
                window.add(0);
                window.add(0);
              }

              if (window.size() != 2) {
                throw new IllegalArgumentException("window must have 2 values"
                    + " in entity for column: "
                    + c.getColumn().getId());
              }
              if (window.get(0) > 0 || window.get(1) > 0) {
                throw new IllegalArgumentException("window days must 0 or negative"
                    + " in entity for column: "
                    + c.getColumn().getId());
              }
              var windowAggregation = c.getWindowAggregation().orElse("MAX");
              if (!Set.of("MAX", "MIN", "SUM", "AVG").contains(windowAggregation.toUpperCase())) {
                throw new IllegalArgumentException(
                    "Invalid window aggregation: " + windowAggregation
                        + " in entity for column: "
                        + c.getColumn().getId());
              }

              return new ActionColumn(new ColumnDtoMapper().apply(c.getColumn()),
                  logicalTable,
                  c.getWhere(), c.getAggregationExpression(),
                  Optional.of(LocalDate.MIN), c.getMissingValue(),
                  window.get(0), window.get(1), windowAggregation,
                  c.getCanMaterialize().orElse(true));
            }, (a, b) -> a, LinkedHashMap::new));

    var totalColumnsMap =
        mergedColumns.getTotalProperties().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            c -> new TotalColumn(
                new ColumnDtoMapper().apply(c.getColumn()),
                c.getSourceColumn(),
                c.getMergeExpression()), (a, b) -> a, LinkedHashMap::new));

    var computedColumnsMap =
        mergedColumns.getComputedProperties().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(), new ColumnComputedDtoMapper(), (a, b) -> a,
            LinkedHashMap::new
        ));

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    firstAppearanceColumnsMap.values().forEach(c -> columns.put(c.getId(), c));
    actionColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in entity");
      }
    });
    totalColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in entity");
      }
    });
    computedColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in entity");
      }
    });

    var kpis = buildKpisMap(dto, mergedKpis);

    var activityLogicalTable = (ActivityLogicalTable) dto.actionLogicalTables()
        .values().stream()
        .filter(d -> d instanceof ActivityLogicalTable)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No action logical table tagged with "
            + ActivityLogicalTable.TAG + " found"));

    return new EntityLogicalTable(
        dto.config().getBaseTablePrefix().replace("{app_id}", appId),
        !columns.isEmpty() ? Optional.of(new Columns(columns)) : Optional.empty(),
        firstAppearanceActionLogicalTable,
        activityLogicalTable,

        // TODO should be able to configure this
        List.of(1, 90),
        List.of(1, 2, 3, 4, 5, 6, 7, 14, 30, 60, 90, 180, 360),

        kpis);
  }

  private Map<String, Kpi> buildKpisMap(EntityDto dto,
                                        List<EntityKpiDto> mergedKpis) {
    Map<String, Kpi> kpis = new HashMap<>();
    for (var kpiDto : mergedKpis) {
      var kpi = new EntityKpiDtoMapper(
          appId, mergedKpis, Optional.empty(), dto.config().getBaseTablePrefix())
          .apply(kpiDto);
      if (!kpis.containsKey(kpi.id())) {
        kpis.put(kpi.id(), kpi);
      } else {
        kpis.get(kpi.id()).merge(kpi);
      }
    }
    return kpis;
  }
}
