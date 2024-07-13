package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnComputedDtoMapper;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisDtoMergeMapper;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisIndexFilterAppender;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisUnfolder;
import com.asemicanalytics.config.mapper.dtomapper.kpi.UnfoldingKpi;
import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
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
    var activityLogicalTable = (ActivityLogicalTable) dto.actionLogicalTables()
        .values().stream()
        .filter(d -> d instanceof ActivityLogicalTable)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No action logical table tagged with "
            + ActivityLogicalTable.TAG + " found"));

    EntityPropertiesDto mergedColumns = new EntityPropertiesDtoMergeMapper().apply(dto.columns());
    SequencedMap<String, Column> columnMap =
        buildColumnsMap(dto, mergedColumns);
    var columns = EntityLogicalTable.withBaseColumns(Optional.of(new Columns(columnMap)),
        firstAppearanceActionLogicalTable, activityLogicalTable);

    // TODO should be able to configure this
    int activeDays = 90;
    List<Integer> cohortDays =
        List.of(0, 1, 2, 3, 4, 5, 6, 7, 14, 21, 28, 30, 40, 50, 60, 90, 120, 180, 270, 360);


    var mergedKpis = new KpisDtoMergeMapper(
        firstAppearanceActionLogicalTable.getDateColumnId()
    ).apply(dto.kpis());
    var unfoldedKpis = new KpisUnfolder(mergedKpis, columns.getColumns().keySet()).unfold();

    var kpis = buildKpisMap(unfoldedKpis);


    new KpisIndexFilterAppender(activeDays, cohortDays, columns.getColumns()).append(kpis);

    return new EntityLogicalTable(
        dto.config().getBaseTablePrefix().replace("{app_id}", appId),
        Optional.of(columns),
        firstAppearanceActionLogicalTable,
        activityLogicalTable,
        activeDays,
        cohortDays,
        kpis);
  }

  private static SequencedMap<String, Column> buildColumnsMap(EntityDto dto,
                                                              EntityPropertiesDto mergedColumns) {

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    addColumns(mergedColumns.getFirstAppearanceProperties().orElse(List.of()).stream()
        .collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            new FirstAppearancePropertyDtoMapper(),
            (a, b) -> a,
            LinkedHashMap::new)), columns);

    addColumns(mergedColumns.getActionProperties().orElse(List.of()).stream()
        .collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            new ActionPropertyDtoMapper(dto.actionLogicalTables()),
            (a, b) -> a,
            LinkedHashMap::new)), columns);

    addColumns(mergedColumns.getSlidingWindowProperties().orElse(List.of()).stream()
        .collect(Collectors.toMap(
            c -> c.getColumn().getId(), new SlidingWindowPropertyDtoMapper(columns),
            (a, b) -> a,
            LinkedHashMap::new)), columns);

    addColumns(mergedColumns.getTotalProperties().orElse(List.of()).stream()
        .collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            new TotalPropertyDtoMapper(),
            (a, b) -> a,
            LinkedHashMap::new)), columns);

    addColumns(mergedColumns.getComputedProperties().orElse(List.of()).stream()
        .collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            new ColumnComputedDtoMapper(),
            (a, b) -> a,
            LinkedHashMap::new)), columns);

    return columns;
  }

  private static void addColumns(Map<String, ? extends Column> sourceMap,
                                 SequencedMap<String, Column> targetMap) {
    sourceMap.values().forEach(c -> {
      if (targetMap.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in entity");
      }
    });
  }

  private Map<String, Kpi> buildKpisMap(List<UnfoldingKpi> kpiList) {
    Map<String, Kpi> kpis = new HashMap<>();
    for (var unfoldingKpi : kpiList) {
      var kpi = unfoldingKpi.buildKpi();
      if (!kpis.containsKey(kpi.id())) {
        kpis.put(kpi.id(), kpi);
      } else {
        kpis.get(kpi.id()).merge(kpi);
      }
    }
    return kpis;
  }
}
