package com.asemicanalytics.config.mapper.dtomapper.kpi;

import java.util.HashSet;
import java.util.Set;

public class FilterPath {

  private final Set<String> filters;

  private FilterPath(Set<String> filters) {
    this.filters = filters;
  }

  public static FilterPath empty() {
    return new FilterPath(Set.of());
  }

  public static FilterPath of(String filter) {
    return new FilterPath(Set.of(filter));
  }

  public FilterPath plus(String filter) {
    Set<String> newFilters = new HashSet<>(filters);
    newFilters.add(filter);
    return new FilterPath(newFilters);
  }

  public FilterPath plus(FilterPath other) {
    Set<String> newFilters = new HashSet<>(filters);
    newFilters.addAll(other.filters);
    return new FilterPath(newFilters);
  }

  public Set<String> getFilters() {
    return filters;
  }

  @Override
  public int hashCode() {
    return filters.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    FilterPath other = (FilterPath) obj;
    return filters.equals(other.filters);
  }
}
