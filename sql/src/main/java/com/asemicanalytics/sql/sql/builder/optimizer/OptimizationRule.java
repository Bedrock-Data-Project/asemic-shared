package com.asemicanalytics.sql.sql.builder.optimizer;

import com.asemicanalytics.sql.sql.builder.select.SelectStatement;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.LinkedHashMap;

public interface OptimizationRule {
  void optimize(LinkedHashMap<String, Cte> ctes, SelectStatement selectStatement);
}
