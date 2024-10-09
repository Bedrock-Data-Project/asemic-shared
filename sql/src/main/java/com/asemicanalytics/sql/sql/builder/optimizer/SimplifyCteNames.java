package com.asemicanalytics.sql.sql.builder.optimizer;

import com.asemicanalytics.sql.sql.builder.tokens.Cte;
import com.asemicanalytics.sql.sql.builder.tokens.SelectStatement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimplifyCteNames implements OptimizationRule {

  @Override
  public void optimize(LinkedHashMap<String, Cte> ctes) {
    Map<String, Integer> counts = new HashMap<>();
    for (var cte : ctes.values()) {
      if (counts.containsKey(cte.tag())) {
        cte.setIndex(counts.get(cte.tag()));
        counts.put(cte.tag(), counts.get(cte.tag()) + 1);
      } else {
        counts.put(cte.tag(), 1);
        cte.setIndex(0);
      }
    }
  }
}
