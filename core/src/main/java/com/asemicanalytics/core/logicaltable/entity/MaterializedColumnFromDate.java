package com.asemicanalytics.core.logicaltable.entity;

import java.time.LocalDate;
import java.util.Optional;

public class MaterializedColumnFromDate implements MaterializedColumnRepository {
  private final Optional<LocalDate> materializedFrom;

  public MaterializedColumnFromDate(Optional<LocalDate> materializedFrom) {
    this.materializedFrom = materializedFrom;
  }

  @Override
  public Optional<LocalDate> materializedFrom(String columnId) {
    return materializedFrom;
  }

  public static MaterializedColumnFromDate from(LocalDate materializedFrom) {
    return new MaterializedColumnFromDate(Optional.of(materializedFrom));
  }

  public static MaterializedColumnFromDate always() {
    return new MaterializedColumnFromDate(Optional.of(LocalDate.MIN));
  }

  public static MaterializedColumnFromDate never() {
    return new MaterializedColumnFromDate(Optional.empty());
  }
}
