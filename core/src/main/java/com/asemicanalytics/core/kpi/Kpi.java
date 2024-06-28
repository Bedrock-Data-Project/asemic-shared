package com.asemicanalytics.core.kpi;

import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import java.util.Map;
import java.util.Optional;

public record Kpi(
    String id,
    Map<String, KpixaxisConfig> xaxisConfig,
    String label,
    Optional<String> category,
    boolean recommended,
    Optional<String> description,
    Optional<Unit> unit,
    boolean hidden
) {
  public boolean isDailyKpi(TemporalLogicalTable logicalTable) {
    return xaxisConfig.containsKey(logicalTable.getDateColumn().getId());
  }

  public boolean isCohortKpi(TemporalLogicalTable logicalTable) {
    return xaxisConfig.containsKey(EntityLogicalTable.COHORT_DAY_COLUMN);
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
