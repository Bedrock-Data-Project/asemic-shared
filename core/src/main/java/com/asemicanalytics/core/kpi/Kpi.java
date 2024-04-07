package com.asemicanalytics.core.kpi;

import com.asemicanalytics.core.datasource.TemporalDatasource;
import java.util.Map;
import java.util.Optional;

public record Kpi(
    String id,
    Map<String, KpixaxisConfig> xaxisConfig,
    String label,
    Optional<String> category,
    boolean recommended,
    Optional<String> description,
    Optional<Unit> unit
) {
  public boolean isDailyKpi(TemporalDatasource datasource) {
    return xaxisConfig.containsKey(datasource.getDateColumn().getId());
  }

  public boolean isCohortKpi(TemporalDatasource datasource) {
    return xaxisConfig.containsKey("cohort_day"); // TODO avoid hardcoding this
  }

  public void merge(Kpi kpi) {
    for (Map.Entry<String, KpixaxisConfig> entry : kpi.xaxisConfig.entrySet()) {
      if (xaxisConfig.containsKey(entry.getKey())) {
        throw new IllegalArgumentException("Kpi already has xaxisConfig for " + entry.getKey());
      } else {
        xaxisConfig.put(entry.getKey(), entry.getValue());
      }
    }
  }
}
