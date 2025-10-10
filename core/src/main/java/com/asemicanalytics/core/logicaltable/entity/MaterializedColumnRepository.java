package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.DisconnectedDateIntervals;
import java.util.Map;

public interface MaterializedColumnRepository {


  Map<String, DisconnectedDateIntervals> materializedOn();

  default DisconnectedDateIntervals materializedOn(String columnId) {
    return materializedOn().getOrDefault(columnId, new DisconnectedDateIntervals());
  }

  default void materialize(DateInterval dateInterval) {

  }
}
