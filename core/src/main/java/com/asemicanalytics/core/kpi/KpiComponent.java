package com.asemicanalytics.core.kpi;

import java.util.Optional;
import java.util.TreeSet;

public record KpiComponent(
    String select,
    TreeSet<String> filters
) {
  public Optional<String> where() {
    return filters.isEmpty()
        ? Optional.empty()
        : Optional.of(String.join(" AND ", filters));
  }
}
