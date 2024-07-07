package com.asemicanalytics.sql.sql.builder.optimizer;

import com.asemicanalytics.sql.sql.builder.select.SelectStatement;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ConsolidateCtes implements OptimizationRule {

  @Override
  public void optimize(LinkedHashMap<String, Cte> ctes, SelectStatement selectStatement) {
    while (true) {
      if (!singleIteration(ctes, selectStatement)) {
        break;
      }
    }
  }

  private boolean singleIteration(LinkedHashMap<String, Cte> ctes,
                                  SelectStatement selectStatement) {
    var uniqueCtes = new LinkedHashMap<String, List<Cte>>();

    for (var cte : ctes.values()) {
      var contentHash = cte.contentHash();
      if (!uniqueCtes.containsKey(contentHash)) {
        uniqueCtes.put(contentHash, new ArrayList<>());
      }
      uniqueCtes.get(contentHash).add(cte);
    }

    for (var cteList : uniqueCtes.values()) {
      if (cteList.size() > 1) {
        for (int i = 1; i < cteList.size(); i++) {
          cteList.getFirst().select().select().merge(cteList.get(i).select().select());
          ctes.remove(cteList.get(i).name());
          selectStatement.swapTable(cteList.get(i), cteList.getFirst());
          int finalI = i;
          ctes.values().forEach(cte -> cte.swapTable(cteList.get(finalI), cteList.getFirst()));
        }
        return true;
      }
    }

    return false;
  }
}
