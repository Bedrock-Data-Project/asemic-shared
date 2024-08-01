package com.asemicanalytics.config;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.config.mapper.FullColumnId;
import com.asemicanalytics.config.mapper.FullKpiId;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.PaymentTransactionActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityModelConfig {
  private final Map<String, ActionLogicalTable> logicalTables;

  private final EntityLogicalTable entityLogicalTable;
  private final FirstAppearanceActionLogicalTable firstAppearanceActionLogicalTable;
  private final ActivityLogicalTable activityActionLogicalTable;
  private final Optional<PaymentTransactionActionLogicalTable>
      paymentTransactionActionLogicalTable;

  public EntityModelConfig(Map<String, ActionLogicalTable> logicalTables,
                           EntityLogicalTable entityLogicalTable,
                           FirstAppearanceActionLogicalTable firstAppearanceActionLogicalTable,
                           ActivityLogicalTable activityActionLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.firstAppearanceActionLogicalTable = firstAppearanceActionLogicalTable;
    this.activityActionLogicalTable = activityActionLogicalTable;
    this.paymentTransactionActionLogicalTable = logicalTables.values().stream()
        .filter(d -> d instanceof PaymentTransactionActionLogicalTable)
        .map(d -> (PaymentTransactionActionLogicalTable) d)
        .findFirst();

    EnrichmentResolver.resolve(logicalTables, entityLogicalTable, enrichmentDefinitions);
  }

  public ActivityLogicalTable activityLogicalTable(String id) {
    if (logicalTables.containsKey(id)) {
      return (ActivityLogicalTable) logicalTables.get(id);
    }
    throw new IllegalArgumentException("No logicalTable named " + id);
  }

  public Map<String, ActionLogicalTable> actionLogicalTables() {
    return logicalTables;
  }

  public EntityLogicalTable getEntityLogicalTable() {
    return entityLogicalTable;
  }

  public FirstAppearanceActionLogicalTable getFirstAppearanceActionLogicalTable() {
    return firstAppearanceActionLogicalTable;
  }

  public ActivityLogicalTable getActivityActionLogicalTable() {
    return activityActionLogicalTable;
  }

  public Optional<PaymentTransactionActionLogicalTable> getPaymentTransactionActionLogicalTable(

  ) {
    return paymentTransactionActionLogicalTable;
  }

  public EntityModelConfig withAdhocSemanticLayer(Columns<EntityProperty> columns) {
    return new EntityModelConfig(
        logicalTables,
        entityLogicalTable.withColumns(columns),
        firstAppearanceActionLogicalTable,
        activityActionLogicalTable,
        List.of());
  }
}
