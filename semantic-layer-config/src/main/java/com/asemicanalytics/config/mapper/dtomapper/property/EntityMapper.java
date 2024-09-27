package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.mapper.dtomapper.kpi.EntityIndexFilterAppender;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisDtoMergeMapper;
import com.asemicanalytics.config.mapper.dtomapper.kpi.KpisUnfolder;
import com.asemicanalytics.config.mapper.dtomapper.kpi.UnfoldingKpi;
import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyDto;
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

  private RegistrationsLogicalTable buildRegistrationsTable(EntityDto dto) {
    return new RegistrationsLogicalTable(
        "entity_registrations",
        TableReference.parse(
            dto.config().getBaseTablePrefix()
                .replace("{app_id}", appId) + "_registrations"),
        dto.eventLogicalTables().getByTag(RegistrationsLogicalTable.TAG)
    );
  }

  private ActivityLogicalTable buildActivityTable(EntityDto dto) {
    return new ActivityLogicalTable(
        "entity_activity",
        TableReference.parse(
            dto.config().getBaseTablePrefix()
                .replace("{app_id}", appId) + "_registrations"),
        dto.eventLogicalTables().getByTag(ActivityLogicalTable.TAG)
    );
  }

  @Override
  public EntityLogicalTable apply(EntityDto dto) {
    int activeDays = dto.config().getActiveDays();
    List<Integer> cohortDays = dto.config().getCohortedDailyKpisDays();

    EntityPropertiesDto mergedColumns = new EntityPropertiesDtoMergeMapper().apply(dto.columns());
    SequencedMap<String, EntityProperty> columnMap =
        buildColumnsMap(dto.eventLogicalTables(), mergedColumns.getProperties()
            .getAdditionalProperties(), Map.of(), dto.config().getActiveDays());

    var registrationsLogicalTable = buildRegistrationsTable(dto);
    var activityLogicalTable = buildActivityTable(dto);
    var columns = EntityLogicalTable.withBaseColumns(Optional.of(new Columns<>(columnMap)),
        registrationsLogicalTable, activityLogicalTable);

    var mergedKpis = new KpisDtoMergeMapper(cohortDays).apply(dto.kpis());
    var unfoldedKpis = new KpisUnfolder(mergedKpis, columns.getColumns().keySet()).unfold();

    var kpis = buildKpisMap(unfoldedKpis);
    new EntityIndexFilterAppender(activeDays, cohortDays, columns.getColumns()).append(kpis);

    return new EntityLogicalTable(
        dto.config().getBaseTablePrefix().replace("{app_id}", appId),
        Optional.of(columns),
        registrationsLogicalTable,
        activityLogicalTable,
        activeDays,
        cohortDays,
        kpis);
  }

  public static SequencedMap<String, EntityProperty> buildColumnsMap(
      EventLogicalTables eventLogicalTables,
      Map<String, EntityPropertyDto> newProperties,
      Map<String, EntityProperty> existingProperties,
      int activeDays) {

    SequencedMap<String, EntityProperty> columns = new LinkedHashMap<>(existingProperties);

    SequencedMap<String, EntityPropertyDto> firstAppearanceProperties = new LinkedHashMap<>();
    SequencedMap<String, EntityPropertyDto> actionProperties = new LinkedHashMap<>();
    SequencedMap<String, EntityPropertyDto> slidingWindowProperties = new LinkedHashMap<>();
    SequencedMap<String, EntityPropertyDto> fixedWindowProperties = new LinkedHashMap<>();
    SequencedMap<String, EntityPropertyDto> lifetimeProperties = new LinkedHashMap<>();
    SequencedMap<String, EntityPropertyDto> computedProperties = new LinkedHashMap<>();

    for (var entry : newProperties.entrySet()) {
      boolean foundPropertyConfig = false;
      if (entry.getValue().getRegistrationProperty().isPresent()) {
        foundPropertyConfig = true;
        firstAppearanceProperties.put(entry.getKey(), entry.getValue());
      }

      if (entry.getValue().getEventProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        actionProperties.put(entry.getKey(), entry.getValue());

      }

      if (entry.getValue().getSlidingWindowProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        slidingWindowProperties.put(entry.getKey(), entry.getValue());

      }

      if (entry.getValue().getFixedWindowProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        fixedWindowProperties.put(entry.getKey(), entry.getValue());

      }

      if (entry.getValue().getLifetimeProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        lifetimeProperties.put(entry.getKey(), entry.getValue());
      }

      if (entry.getValue().getComputedProperty().isPresent()) {
        if (foundPropertyConfig) {
          throw new IllegalArgumentException(
              "Duplicate property config for column: " + entry.getKey());
        }
        foundPropertyConfig = true;
        computedProperties.put(entry.getKey(), entry.getValue());
      }

      if (!foundPropertyConfig) {
        throw new IllegalArgumentException("No property config for column: " + entry.getKey());
      }
    }

    for (var entry : firstAppearanceProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      columns.put(entry.getKey(), new RegistrationPropertyDtoMapper(column).apply(
          entry.getValue().getRegistrationProperty().get()));
    }
    for (var entry : actionProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      columns.put(entry.getKey(), new EventPropertyDtoMapper(column, eventLogicalTables, false)
          .apply(entry.getValue().getEventProperty().get()));
    }
    for (var entry : lifetimeProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      var entity = new LifetimePropertyDtoMapper(column, eventLogicalTables)
          .apply(entry.getValue().getLifetimeProperty().get());
      columns.put(entry.getKey(), entity);
      columns.put(entity.getSourceColumn().getId(), entity.getSourceColumn());
    }
    for (var entry : slidingWindowProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      var entity = new SlidingWindowPropertyDtoMapper(column, activeDays, eventLogicalTables)
          .apply(entry.getValue().getSlidingWindowProperty().get());
      columns.put(entry.getKey(), entity);
      columns.put(entity.getSourceColumn().getId(), entity.getSourceColumn());
    }
    for (var entry : fixedWindowProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      var entity = new FixedWindowPropertyDtoMapper(column, eventLogicalTables)
          .apply(entry.getValue().getFixedWindowProperty().get());
      columns.put(entry.getKey(), entity);
      columns.put(entity.getSourceColumn().getId(), entity.getSourceColumn());
    }
    for (var entry : computedProperties.entrySet()) {
      var column = buildColumn(entry.getKey(), entry.getValue());
      columns.put(entry.getKey(), new ComputedPropertyDtoMapper(column)
          .apply(entry.getValue().getComputedProperty().get()));
    }

    return columns;
  }

  private static Column buildColumn(String id, EntityPropertyDto propertyDto) {
    return new Column(
        id,
        DataType.valueOf(propertyDto.getDataType().name()),
        DefaultLabel.of(propertyDto.getLabel(), id),
        propertyDto.getDescription(),
        propertyDto.getCanFilter().orElse(true),
        propertyDto.getCanGroupBy().orElse(false),
        Set.of()
    );
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
