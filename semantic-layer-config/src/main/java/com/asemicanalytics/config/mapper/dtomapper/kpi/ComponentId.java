package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.core.kpi.KpiComponent;
import java.util.Set;
import java.util.TreeSet;

public class ComponentId {
  private static final String PROPERTY_PREFIX = "property";
  private static final String KPI_PREFIX = "kpi";

  private final String type;
  private final String id;

  public ComponentId(String componentId, Set<String> propertyIds, Set<String> kpiIds) {
    var token = componentId.split("\\.");
    if (token.length != 2
        || (!token[0].equals(PROPERTY_PREFIX) && !token[0].equals(KPI_PREFIX))) {
      throw new IllegalArgumentException("Invalid component id: " + componentId
          + ". Must prefix with property. or kpi.");
    }

    this.type = token[0];
    this.id = token[1];

    if (isProperty() && !propertyIds.contains(this.id)) {
      throw new IllegalArgumentException("Property not found: " + token[1]);
    }

    if (isKpi() && !kpiIds.contains(this.id)) {
      throw new IllegalArgumentException("Kpi not found: " + token[1]);
    }
  }

  public boolean isProperty() {
    return type.equals(PROPERTY_PREFIX);
  }

  public boolean isKpi() {
    return type.equals(KPI_PREFIX);
  }

  public String getId() {
    return id;
  }

  public KpiComponent buildKpiComponent(FilterPath filterPath) {
    return new KpiComponent("{" + id + "}", new TreeSet<>(filterPath.getFilters()));
  }

  @Override
  public int hashCode() {
    return type.hashCode() + id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ComponentId other = (ComponentId) obj;
    return type.equals(other.type) && id.equals(other.id);
  }

  public String toString() {
    return type + "." + id;
  }
}
