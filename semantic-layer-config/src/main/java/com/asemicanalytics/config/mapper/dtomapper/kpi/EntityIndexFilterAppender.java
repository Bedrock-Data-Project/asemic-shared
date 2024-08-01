package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.entity.ComputedColumn;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EntityIndexFilterAppender {
  private final int activeDays;
  private final List<Integer> cohortDays;
  private final Map<String, EntityProperty> columns;

  public EntityIndexFilterAppender(int activeDays, List<Integer> cohortDays,
                                   Map<String, EntityProperty> columns) {
    this.activeDays = activeDays;
    this.cohortDays = cohortDays;
    this.columns = columns;
  }

  public Set<String> getFilters(String formula) {
    var properties = Formatter.extractKeys(formula);
    var allFilters = new TreeSet<String>();
    for (var propertyId : properties) {
      var column = columns.get(propertyId);
      Set<String> filters = switch (column.getType()) {
        case COMPUTED -> getFilters(((ComputedColumn) column).getFormula());
        case FIRST_APPEARANCE, LIFETIME -> Set.of();
        case ACTION -> Set.of(EntityLogicalTable.dailyIndexFilter());
        case SLIDING_WINDOW -> Set.of(EntityLogicalTable.activeIndexFilter(activeDays));
      };
      allFilters.addAll(filters);
    }
    return allFilters;
  }

  public void append(Map<String, Kpi> kpis) {
    for (var kpi : kpis.values()) {
      for (var xaxis : kpi.xaxisConfig().entrySet()) {
        for (var component : xaxis.getValue().components().values()) {
          if (xaxis.getKey().equals(EntityLogicalTable.COHORT_DAY_COLUMN)) {
            component.filters().add(EntityLogicalTable.cohortIndexFilter(cohortDays));
          } else {
            if (component.where().isPresent()
                && component.where().get()
                .matches("\\{" + EntityLogicalTable.COHORT_DAY_COLUMN + "\\} = \\d+")) {
              var cohortDay = Integer.parseInt(component.where().get().split(" = ")[1]);
              if (cohortDay <= activeDays) {
                component.filters().add(EntityLogicalTable.activeIndexFilter(activeDays));
              } else {
                component.filters().add(EntityLogicalTable.cohortIndexFilter(cohortDays));
              }
            } else {
              component.filters().addAll(getFilters(component.select()));
            }
          }
        }
      }
    }
  }
}
