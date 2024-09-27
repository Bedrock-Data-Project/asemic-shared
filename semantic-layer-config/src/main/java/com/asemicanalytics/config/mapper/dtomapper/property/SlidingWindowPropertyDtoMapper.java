package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.RelativeDaysInterval;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.SlidingWindowColumn;
import com.asemicanalytics.core.logicaltable.entity.WindowAggregationFunction;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import java.util.function.Function;

public class SlidingWindowPropertyDtoMapper implements
    Function<EntityPropertySlidingWindowDto, SlidingWindowColumn> {

  private final int activeDays;
  private final Column column;
  private final EventLogicalTables eventLogicalTables;


  public SlidingWindowPropertyDtoMapper(Column column,
                                        int activeDays,
                                        EventLogicalTables eventLogicalTables) {
    this.column = column;
    this.activeDays = activeDays;
    this.eventLogicalTables = eventLogicalTables;
  }

  @Override
  public SlidingWindowColumn apply(
      EntityPropertySlidingWindowDto dto) {

    EntityProperty sourceColumn = ComposableColumnHelper.getSourceColumn(
        column,
        dto.getSourceProperty(),
        dto.getSourceComputedProperty(),
        dto.getSourceEventProperty(),
        eventLogicalTables
    );

    if (dto.getRelativeDaysTo() < -activeDays) {
      throw new IllegalArgumentException(
          "window cannot go in the past more than active days"
              + " in entity for column: "
              + column.getId());
    }

    return new SlidingWindowColumn(column,
        sourceColumn,
        new RelativeDaysInterval(dto.getRelativeDaysFrom(), dto.getRelativeDaysTo()),
        WindowAggregationFunction.valueOf(
            dto.getWindowFunction().name()));
  }
}
