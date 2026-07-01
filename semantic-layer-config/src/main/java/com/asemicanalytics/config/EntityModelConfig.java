package com.asemicanalytics.config;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.config.mapper.dtomapper.property.EntityMapper;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityModelConfig {
  private final EventLogicalTables logicalTables;

  private final EntityLogicalTable entityLogicalTable;
  private final RegistrationsLogicalTable registrationLogicalTable;
  private final ActivityLogicalTable activityActionLogicalTable;
  // Merged (post cohort-day expansion), pre-unfold published KPI DTOs, retained so a
  // request-time ad-hoc KPI overlay can re-compile published + draft metrics together.
  private final Map<String, KpiDto> kpiDtos;

  public EntityModelConfig(EventLogicalTables logicalTables,
                           EntityLogicalTable entityLogicalTable,
                           RegistrationsLogicalTable registrationLogicalTable,
                           ActivityLogicalTable activityActionLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions) {
    this(logicalTables, entityLogicalTable, registrationLogicalTable,
        activityActionLogicalTable, enrichmentDefinitions, Map.of());
  }

  public EntityModelConfig(EventLogicalTables logicalTables,
                           EntityLogicalTable entityLogicalTable,
                           RegistrationsLogicalTable registrationLogicalTable,
                           ActivityLogicalTable activityActionLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions,
                           Map<String, KpiDto> kpiDtos) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.registrationLogicalTable = registrationLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
    this.kpiDtos = kpiDtos;

    EnrichmentResolver.resolve(logicalTables.getEventLogicalTables(), entityLogicalTable,
        enrichmentDefinitions);
  }

  private EntityModelConfig(
      EventLogicalTables logicalTables,
      EntityLogicalTable entityLogicalTable,
      RegistrationsLogicalTable registrationLogicalTable,
      ActivityLogicalTable activityActionLogicalTable,
      Map<String, KpiDto> kpiDtos) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.registrationLogicalTable = registrationLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
    this.kpiDtos = kpiDtos;
  }

  public EventLogicalTables eventLogicalTables() {
    return logicalTables;
  }

  public EntityLogicalTable getEntityLogicalTable() {
    return entityLogicalTable;
  }

  public RegistrationsLogicalTable getRegistrationLogicalTable() {
    return registrationLogicalTable;
  }

  public ActivityLogicalTable getActivityActionLogicalTable() {
    return activityActionLogicalTable;
  }

  public EntityModelConfig withAdhocSemanticLayer(Columns<EntityProperty> columns) {
    return new EntityModelConfig(
        logicalTables,
        entityLogicalTable.withColumns(columns),
        registrationLogicalTable,
        activityActionLogicalTable,
        kpiDtos);
  }

  // Request-time ad-hoc KPI overlay: merge the given draft/inline KPI DTOs over the
  // retained published DTOs (draft wins on id) and re-run the canonical compile against
  // the CURRENT columns — which already include any ad-hoc properties merged earlier —
  // so `{kpi.published}`, `{kpi.draft}` and `{property.adhoc}` all resolve one way.
  public EntityModelConfig withAdhocKpis(Map<String, KpiDto> adhocKpiDtos) {
    Map<String, KpiDto> merged = new HashMap<>(kpiDtos);
    merged.putAll(adhocKpiDtos);
    var kpis = EntityMapper.compileKpis(
        merged,
        entityLogicalTable.getColumns(),
        entityLogicalTable.getActivityTableDays(),
        entityLogicalTable.getCohortTableDays());
    return new EntityModelConfig(
        logicalTables,
        entityLogicalTable.withKpis(kpis),
        registrationLogicalTable,
        activityActionLogicalTable,
        merged);
  }
}
