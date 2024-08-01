package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.mapper.dtomapper.kpi.EntityIndexFilterAppender;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisDtoMergeMapper;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisUnfolder;
import com.asemicanalytics.config.mapper.dtomapper.kpi.UnfoldingKpi;
import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;

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

    // TODO should be able to configure this
    int activeDays = dto.config().getActiveDays();
    List<Integer> cohortDays = dto.config().getCohortedDailyKpisDays();

    EntityPropertiesDto mergedColumns = new EntityPropertiesDtoMergeMapper().apply(dto.columns());
    SequencedMap<String, EntityProperty> columnMap =
        buildColumnsMap(dto, mergedColumns, activeDays);
    var columns = EntityLogicalTable.withBaseColumns(Optional.of(new Columns<>(columnMap)),
        firstAppearanceActionLogicalTable, activityLogicalTable);

    var mergedKpis = new KpisDtoMergeMapper(cohortDays).apply(dto.kpis());
    var unfoldedKpis = new KpisUnfolder(mergedKpis, columns.getColumns().keySet()).unfold();

    var kpis = buildKpisMap(unfoldedKpis);
    new EntityIndexFilterAppender(activeDays, cohortDays, columns.getColumns()).append(kpis);

    return new EntityLogicalTable(
        dto.config().getBaseTablePrefix().replace("{app_id}", appId),
        Optional.of(columns),
        firstAppearanceActionLogicalTable,
        activityLogicalTable,
        activeDays,
        cohortDays,
        kpis);
  }

  private static SequencedMap<String, EntityProperty> buildColumnsMap(
      EntityDto dto, EntityPropertiesDto mergedColumns, int activeDays) {

    SequencedMap<String, EntityProperty> columns = new LinkedHashMap<>();

    for (var entry : mergedColumns.getProperties().getAdditionalProperties().entrySet()) {
      var column = new Column(
          entry.getKey(),
          DataType.valueOf(entry.getValue().getDataType().name()),
          DefaultLabel.of(entry.getValue().getLabel(), entry.getKey()),
          entry.getValue().getDescription(),
          entry.getValue().getCanFilter().orElse(true),
          entry.getValue().getCanGroupBy().orElse(false),
          Set.of()
      );

      boolean foundPropertyConfig = false;
      if (entry.getValue().getFirstAppearanceProperty().isPresent()) {
        foundPropertyConfig = true;
        columns.put(entry.getKey(), new FirstAppearancePropertyDtoMapper(column).apply(
            entry.getValue().getFirstAppearanceProperty().get()));
      }

      if (entry.getValue().getActionProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        columns.put(entry.getKey(), new ActionPropertyDtoMapper(column, dto.actionLogicalTables())
            .apply(entry.getValue().getActionProperty().get()));
      }

      if (entry.getValue().getSlidingWindowProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        columns.put(entry.getKey(), new SlidingWindowPropertyDtoMapper(column, columns, activeDays)
            .apply(entry.getValue().getSlidingWindowProperty().get()));
      }

      if (entry.getValue().getLifetimeProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        columns.put(entry.getKey(), new LifetimePropertyDtoMapper(column, dto.actionLogicalTables())
            .apply(entry.getValue().getLifetimeProperty().get()));
      }

      if (entry.getValue().getComputedProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        columns.put(entry.getKey(), new ComputedPropertyDtoMapper(column)
            .apply(entry.getValue().getComputedProperty().get()));
      }
    }

    return columns;
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
