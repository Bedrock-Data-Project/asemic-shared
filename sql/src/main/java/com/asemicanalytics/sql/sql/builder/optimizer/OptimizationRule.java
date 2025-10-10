package com.asemicanalytics.sql.sql.builder.optimizer;

import com.asemicanalytics.sql.sql.builder.tokens.Cte;
import java.util.LinkedHashMap;

public interface OptimizationRule {
  void optimize(LinkedHashMap<String, Cte> ctes);
}
