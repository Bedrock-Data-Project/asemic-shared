package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.FirstAppearanceColumn;
import com.asemicanalytics.core.logicaltable.entity.SlidingWindowColumn;
import com.asemicanalytics.core.logicaltable.entity.TotalColumn;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import java.util.List;
import java.util.Map;

public class KpisIndexFilterAppender {
  private final int activeDays;
  private final List<Integer> cohortDays;
  private final Map<String, Column> columns;

  public KpisIndexFilterAppender(int activeDays, List<Integer> cohortDays,
                                 Map<String, Column> columns) {
    this.activeDays = activeDays;
    this.cohortDays = cohortDays;
    this.columns = columns;
  }

  private void appendFilter(KpiComponent component, String formula) {
    var properties = Formatter.extractKeys(formula);
    for (var propertyId : properties) {
      var column = columns.get(propertyId);
      if (column instanceof ComputedColumn computedColumn) {
        appendFilter(component, computedColumn.getFormula());
      } else if (column instanceof ActionColumn) {
        component.filters().add(EntityLogicalTable.dailyIndexFilter());
      } else if (column instanceof SlidingWindowColumn) {
        component.filters().add(EntityLogicalTable.activeIndexFilter(activeDays));
      } else if (column instanceof FirstAppearanceColumn) {
        // totals table
      } else if (column instanceof TotalColumn) {
        // totals table
      } else {
        throw new IllegalArgumentException("Unknown column type: " + column);
      }
    }
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
              appendFilter(component, component.select());
            }
          }
        }
      }
    }
  }
}
