package com.asemicanalytics.config;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.event.FirstAppearanceEventLogicalTable;
import com.asemicanalytics.core.logicaltable.event.PaymentTransactionEventLogicalTable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityModelConfig {
  private final Map<String, EventLogicalTable> logicalTables;

  private final EntityLogicalTable entityLogicalTable;
  private final FirstAppearanceEventLogicalTable firstAppearanceActionLogicalTable;
  private final ActivityLogicalTable activityActionLogicalTable;
  private final Optional<PaymentTransactionEventLogicalTable>
      paymentTransactionActionLogicalTable;

  public EntityModelConfig(Map<String, EventLogicalTable> logicalTables,
                           EntityLogicalTable entityLogicalTable,
                           FirstAppearanceEventLogicalTable firstAppearanceActionLogicalTable,
                           ActivityLogicalTable activityActionLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.firstAppearanceActionLogicalTable = firstAppearanceActionLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
    this.paymentTransactionActionLogicalTable = logicalTables.values().stream()
        .filter(d -> d instanceof PaymentTransactionEventLogicalTable)
        .map(d -> (PaymentTransactionEventLogicalTable) d)
        .findFirst();

    EnrichmentResolver.resolve(logicalTables, entityLogicalTable, enrichmentDefinitions);
  }

  private EntityModelConfig(
      Map<String, EventLogicalTable> logicalTables,
      EntityLogicalTable entityLogicalTable,
      FirstAppearanceEventLogicalTable firstAppearanceActionLogicalTable,
      ActivityLogicalTable activityActionLogicalTable,
      Optional<PaymentTransactionEventLogicalTable> paymentTransactionActionLogicalTable) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.firstAppearanceActionLogicalTable = firstAppearanceActionLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
    this.paymentTransactionActionLogicalTable = paymentTransactionActionLogicalTable;
  }

  public ActivityLogicalTable activityLogicalTable(String id) {
    if (logicalTables.containsKey(id)) {
      return (ActivityLogicalTable) logicalTables.get(id);
    }
    throw new IllegalArgumentException("No logicalTable named " + id);
  }

  public Map<String, EventLogicalTable> actionLogicalTables() {
    return logicalTables;
  }

  public EntityLogicalTable getEntityLogicalTable() {
    return entityLogicalTable;
  }

  public FirstAppearanceEventLogicalTable getFirstAppearanceActionLogicalTable() {
    return firstAppearanceActionLogicalTable;
  }

  public ActivityLogicalTable getActivityActionLogicalTable() {
    return activityActionLogicalTable;
  }

  public Optional<PaymentTransactionEventLogicalTable> getPaymentTransactionActionLogicalTable(

  ) {
    return paymentTransactionActionLogicalTable;
  }

  public EntityModelConfig withAdhocSemanticLayer(Columns<EntityProperty> columns) {
    return new EntityModelConfig(
        logicalTables,
        entityLogicalTable.withColumns(columns),
        firstAppearanceActionLogicalTable,
        activityActionLogicalTable,
        paymentTransactionActionLogicalTable);
  }
}
