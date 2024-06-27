package com.asemicanalytics.config;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.config.mapper.ColumnReference;
import com.asemicanalytics.config.mapper.FullColumnId;
import com.asemicanalytics.config.mapper.FullKpiId;
import com.asemicanalytics.config.mapper.KpiReference;
import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.PaymentTransactionActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityModelConfig {
  private final Map<String, ActionLogicalTable> logicalTables;

  private final Optional<EntityLogicalTable> entityLogicalTable;
  private final Optional<FirstAppearanceActionLogicalTable> firstAppearanceActionLogicalTable;
  private final Optional<ActivityLogicalTable> activityActionLogicalTable;
  private final Optional<PaymentTransactionActionLogicalTable>
      paymentTransactionActionLogicalTable;

  public EntityModelConfig(Map<String, ActionLogicalTable> logicalTables,
                           Optional<EntityLogicalTable> entityLogicalTable,
                           List<EnrichmentDefinition> enrichmentDefinitions) {
    this.logicalTables = logicalTables;
    this.entityLogicalTable = entityLogicalTable;
    this.firstAppearanceActionLogicalTable = logicalTables.values().stream()
        .filter(d -> d instanceof FirstAppearanceActionLogicalTable)
        .map(d -> (FirstAppearanceActionLogicalTable) d)
        .findFirst();
    this.activityActionLogicalTable = logicalTables.values().stream()
        .filter(d -> d instanceof ActivityLogicalTable)
        .map(d -> (ActivityLogicalTable) d)
        .findFirst();
    this.paymentTransactionActionLogicalTable = logicalTables.values().stream()
        .filter(d -> d instanceof PaymentTransactionActionLogicalTable)
        .map(d -> (PaymentTransactionActionLogicalTable) d)
        .findFirst();

    EnrichmentResolver.resolve(logicalTables, entityLogicalTable, enrichmentDefinitions);
  }

  public LogicalTable logicalTable(String id) {
    if (logicalTables.containsKey(id)) {
      return logicalTables.get(id);
    }
    if (entityLogicalTable.isPresent() && entityLogicalTable.get().getId().equals(id)) {
      return entityLogicalTable.get();
    }
    throw new IllegalArgumentException("No logicalTable named " + id);
  }

  public TemporalLogicalTable temporalLogicalTable(String id) {
    var logicalTable = logicalTable(id);
    if (!(logicalTable instanceof TemporalLogicalTable)) {
      throw new IllegalArgumentException("LogicalTable " + id + " is not a temporal logicalTable");
    }
    return (TemporalLogicalTable) logicalTable;
  }

  public ColumnReference column(FullColumnId fullColumnId) {
    var logicalTable = logicalTable(fullColumnId.logicalTableId());
    return new ColumnReference(logicalTable,
        logicalTable.getColumns().column(fullColumnId.columnId()).getId());
  }

  public KpiReference kpi(FullKpiId fullKpiId) {
    var logicalTable = temporalLogicalTable(fullKpiId.logicalTableId());
    return new KpiReference(logicalTable.getId(), logicalTable.kpi(fullKpiId.kpiId()));
  }

  public List<LogicalTable> logicalTables() {
    List<LogicalTable> logicalTables = new ArrayList<>(this.logicalTables.values());
    entityLogicalTable.ifPresent(logicalTables::add);
    return logicalTables;
  }

  public List<TemporalLogicalTable> temporalLogicalTables() {
    return logicalTables().stream()
        .filter(d -> d instanceof TemporalLogicalTable)
        .map(d -> (TemporalLogicalTable) d)
        .collect(Collectors.toList());
  }

  public Optional<EntityLogicalTable> getEntityLogicalTable() {
    return entityLogicalTable;
  }

  public Optional<FirstAppearanceActionLogicalTable> getFirstAppearanceActionLogicalTable() {
    return firstAppearanceActionLogicalTable;
  }

  public Optional<ActivityLogicalTable> getActivityActionLogicalTable() {
    return activityActionLogicalTable;
  }

  public Optional<PaymentTransactionActionLogicalTable> getPaymentTransactionActionLogicalTable(

  ) {
    return paymentTransactionActionLogicalTable;
  }
}
