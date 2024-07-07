package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import com.asemicanalytics.sql.sql.builder.tokens.Token;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Formula {
  private final FilterPath filterPath;
  private String formula;
  private final Map<String, KpiComponent> kpiComponentMap = new TreeMap<>();
  private final Map<String, Token> resolvedComponents = new TreeMap<>();
  private boolean rendered;

  public Formula(FilterPath filterPath, String formula) {
    this.formula = formula;
    this.filterPath = filterPath;
  }

  public Map<String, KpiComponent> getKpiComponentMap() {
    return Collections.unmodifiableMap(kpiComponentMap);
  }

  public void addKpiComponent(String key, KpiComponent value) {
    TreeSet<String> filters = new TreeSet<>(value.filters());
    filters.addAll(filterPath.getFilters());
    var mergedKpiComponent = new KpiComponent(value.select(), filters);

    if (rendered) {
      throw new IllegalStateException("Formula is already rendered");
    }

    if (kpiComponentMap.containsKey(key) && !kpiComponentMap.get(key).equals(mergedKpiComponent)) {
      throw new IllegalArgumentException("Kpi component with key " + key + " already exists");
    }
    kpiComponentMap.put(key, mergedKpiComponent);
  }

  public void addResolvedComponent(String key, Token value) {
    if (rendered) {
      throw new IllegalStateException("Formula is already rendered");
    }

    if (resolvedComponents.containsKey(key) && !resolvedComponents.get(key).equals(value)) {
      throw new IllegalArgumentException("Resolved component with key " + key + " already exists");
    }
    resolvedComponents.put(key, value);
  }

  public String render() {

    if (rendered) {
      return formula;
    }

    formula = Formatter.format(formula, TemplateDict.noMissing(resolvedComponents), null);
    rendered = true;
    return formula;
  }

  public boolean rendered() {
    return rendered;
  }
}
