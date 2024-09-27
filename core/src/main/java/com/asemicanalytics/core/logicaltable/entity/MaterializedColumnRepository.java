package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DisconnectedDateIntervals;
import java.time.LocalDate;
import java.util.Map;

public interface MaterializedColumnRepository {


  Map<String, DisconnectedDateIntervals> materializedOn();

  default DisconnectedDateIntervals materializedOn(String columnId) {
    return materializedOn().getOrDefault(columnId, new DisconnectedDateIntervals());
  }

  default void materialize(LocalDate date) {

  }
}
