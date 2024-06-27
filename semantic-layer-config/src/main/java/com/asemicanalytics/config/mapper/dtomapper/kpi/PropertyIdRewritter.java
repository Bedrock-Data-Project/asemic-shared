package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.core.kpi.KpiComponent;
import java.util.HashMap;
import java.util.Map;

public class PropertyIdRewritter {
  private int currentId = 0;
  private final Map<KpiComponent, String> compenentIds = new HashMap<>();


  public String rewrite(ComponentId componentId, KpiComponent kpiComponent) {
    if (!compenentIds.containsKey(kpiComponent)) {
      String rewrittenId = componentId.getId() + "_" + nextId();
      compenentIds.put(kpiComponent, rewrittenId);
    }

    return compenentIds.get(kpiComponent);
  }

  public void reset() {
    currentId = 0;
    compenentIds.clear();
  }

  private int nextId() {
    return currentId++;
  }
}
