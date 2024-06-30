package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;
import java.time.LocalDate;
import java.util.Optional;

public class SlidingWindowColumn extends Column {
  private final ActionColumn sourceColumn;
  private final Optional<LocalDate> materializedFrom;
  private final int relativeDaysFrom;
  private final int relativeDaysTo;
  private final String windowAggregation;

  public SlidingWindowColumn(
      Column column,
      ActionColumn sourceColumn,
      int relativeDaysFrom,
      int relativeDaysTo,
      String windowAggregation,
      Optional<LocalDate> materializedFrom) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.sourceColumn = sourceColumn;
    this.materializedFrom = materializedFrom;
    this.relativeDaysFrom = relativeDaysFrom;
    this.relativeDaysTo = relativeDaysTo;
    this.windowAggregation = windowAggregation;
  }

  public Optional<LocalDate> getMaterializedFrom() {
    return materializedFrom;
  }

  public int getRelativeDaysFrom() {
    return relativeDaysFrom;
  }

  public int getRelativeDaysTo() {
    return relativeDaysTo;
  }

  public String getWindowAggregation() {
    return windowAggregation;
  }

  public ActionColumn getSourceColumn() {
    return sourceColumn;
  }
}