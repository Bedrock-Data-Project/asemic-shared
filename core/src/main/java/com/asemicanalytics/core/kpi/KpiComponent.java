package com.asemicanalytics.core.kpi;

import java.util.Optional;
import java.util.TreeSet;

public class KpiComponent {
  private final String select;
  private final TreeSet<String> filters;

  public KpiComponent(String select, TreeSet<String> filters) {
    this.select = select;
    this.filters = filters;
  }

  public Optional<String> where() {
    return filters.isEmpty()
        ? Optional.empty()
        : Optional.of(String.join(" AND ", filters));
  }

  public String select() {
    return select;
  }

  public TreeSet<String> filters() {
    return filters;
  }

  @Override
  public String toString() {
    return "KpiComponent{"
        + "select='" + select + '\''
        + ", filters=" + filters
        + '}';
  }

  @Override
  public int hashCode() {
    return select.hashCode() + filters.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    KpiComponent that = (KpiComponent) obj;
    return select.equals(that.select)
        && filters.equals(that.filters);
  }
}
