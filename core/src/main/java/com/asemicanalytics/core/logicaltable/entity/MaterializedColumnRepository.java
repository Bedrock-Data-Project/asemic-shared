package com.asemicanalytics.core.logicaltable.entity;

import java.time.LocalDate;
import java.util.Optional;

public interface MaterializedColumnRepository {
  Optional<LocalDate> materializedFrom(String columnId);
}
