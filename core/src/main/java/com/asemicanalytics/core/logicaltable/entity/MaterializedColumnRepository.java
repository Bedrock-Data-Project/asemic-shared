package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.DisconnectedDateIntervals;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.logicaltable.LogicalTable;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface MaterializedColumnRepository {


  Map<String, DisconnectedDateIntervals> materializedOn();

  default DisconnectedDateIntervals materializedOn(String columnId) {
    return materializedOn().getOrDefault(columnId, new DisconnectedDateIntervals());
  }

  default void materialize(LocalDate date) {

  }
}
