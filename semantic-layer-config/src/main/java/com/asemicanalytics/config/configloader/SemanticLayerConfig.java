package com.asemicanalytics.config.configloader;

import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.enrichment.EnrichmentResolver;
import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.core.datasource.TemporalDatasource;
import com.asemicanalytics.core.datasource.useraction.ActivityUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.PaymentTransactionUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.RegistrationUserActionDatasource;
import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SemanticLayerConfig {
  private final Map<String, Datasource> datasources;

  private final Optional<UserWideDatasource> userWideDatasource;
  private final Optional<RegistrationUserActionDatasource> registrationUserActionDatasource;
  private final Optional<ActivityUserActionDatasource> activityUserActionDatasource;
  private final Optional<PaymentTransactionUserActionDatasource>
      paymentTransactionUserActionDatasource;

  public SemanticLayerConfig(Map<String, Datasource> datasources,
                             Optional<UserWideDatasource> userWideDatasource,
                             List<EnrichmentDefinition> enrichmentDefinitions) {
    this.datasources = datasources;
    this.userWideDatasource = userWideDatasource;
    this.registrationUserActionDatasource = datasources.values().stream()
        .filter(d -> d instanceof RegistrationUserActionDatasource)
        .map(d -> (RegistrationUserActionDatasource) d)
        .findFirst();
    this.activityUserActionDatasource = datasources.values().stream()
        .filter(d -> d instanceof ActivityUserActionDatasource)
        .map(d -> (ActivityUserActionDatasource) d)
        .findFirst();
    this.paymentTransactionUserActionDatasource = datasources.values().stream()
        .filter(d -> d instanceof PaymentTransactionUserActionDatasource)
        .map(d -> (PaymentTransactionUserActionDatasource) d)
        .findFirst();

    EnrichmentResolver.resolve(datasources, userWideDatasource, enrichmentDefinitions);
  }

  public Datasource datasource(String id) {
    if (datasources.containsKey(id)) {
      return datasources.get(id);
    }
    if (userWideDatasource.isPresent() && userWideDatasource.get().getId().equals(id)) {
      return userWideDatasource.get();
    }
    throw new IllegalArgumentException("No datasource named " + id);
  }

  public TemporalDatasource temporalDatasource(String id) {
    var datasource = datasource(id);
    if (!(datasource instanceof TemporalDatasource)) {
      throw new IllegalArgumentException("Datasource " + id + " is not a temporal datasource");
    }
    return (TemporalDatasource) datasource;
  }

  public ColumnReference column(FullColumnId fullColumnId) {
    var datasource = datasource(fullColumnId.datasourceId());
    return new ColumnReference(datasource,
        datasource.getColumns().column(fullColumnId.columnId()).getId());
  }

  public KpiReference kpi(FullKpiId fullKpiId) {
    var datasource = temporalDatasource(fullKpiId.datasourceId());
    return new KpiReference(datasource.getId(), datasource.kpi(fullKpiId.kpiId()));
  }

  public List<Datasource> datasources() {
    var datasources = new ArrayList<>(this.datasources.values());
    userWideDatasource.ifPresent(datasources::add);
    return datasources;
  }

  public List<TemporalDatasource> temporalDatasources() {
    return datasources().stream()
        .filter(d -> d instanceof TemporalDatasource)
        .map(d -> (TemporalDatasource) d)
        .collect(Collectors.toList());
  }

  public Optional<UserWideDatasource> getUserWideDatasource() {
    return userWideDatasource;
  }

  public Optional<RegistrationUserActionDatasource> getRegistrationUserActionDatasource() {
    return registrationUserActionDatasource;
  }

  public Optional<ActivityUserActionDatasource> getActivityUserActionDatasource() {
    return activityUserActionDatasource;
  }

  public Optional<PaymentTransactionUserActionDatasource> getPaymentTransactionUserActionDatasource(

  ) {
    return paymentTransactionUserActionDatasource;
  }
}
