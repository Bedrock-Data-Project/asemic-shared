package com.asemicanalytics.config;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import java.util.List;

public class EntityModelConfig {
  private final EventLogicalTables logicalTables;

  private final EntityLogicalTable entityLogicalTable;
  private final RegistrationsLogicalTable registrationLogicalTable;
  private final ActivityLogicalTable activityActionLogicalTable;

  public EntityModelConfig(EventLogicalTables logicalTables,
                           EntityLogicalTable entityLogicalTable,
                           RegistrationsLogicalTable registrationLogicalTable,
                           ActivityLogicalTable activityActionLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.registrationLogicalTable = registrationLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;

    EnrichmentResolver.resolve(logicalTables.getEventLogicalTables(), entityLogicalTable,
        enrichmentDefinitions);
  }

  private EntityModelConfig(
      EventLogicalTables logicalTables,
      EntityLogicalTable entityLogicalTable,
      RegistrationsLogicalTable registrationLogicalTable,
      ActivityLogicalTable activityActionLogicalTable) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.registrationLogicalTable = registrationLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
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
        activityActionLogicalTable);
  }
}
