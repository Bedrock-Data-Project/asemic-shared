package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.RelativeDaysInterval;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.SlidingWindowColumn;
import com.asemicanalytics.core.logicaltable.entity.WindowAggregationFunction;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import java.util.Map;
import java.util.function.Function;

public class SlidingWindowPropertyDtoMapper implements
    Function<EntityPropertySlidingWindowDto, SlidingWindowColumn> {

  private final Map<String, EntityProperty> columns;
  private final int activeDays;
  private final Column column;

  public SlidingWindowPropertyDtoMapper(Column column, Map<String, EntityProperty> columns,
                                        int activeDays) {
    this.column = column;
    this.columns = columns;
    this.activeDays = activeDays;
  }

  @Override
  public SlidingWindowColumn apply(
      EntityPropertySlidingWindowDto dto) {

    if (!columns.containsKey(dto.getSourceProperty())) {
      throw new IllegalArgumentException("Source property not found: "
          + dto.getSourceProperty()
          + " in entity for column: "
          + column.getId());
    }

    if (dto.getRelativeDaysTo() < -activeDays) {
      throw new IllegalArgumentException(
          "window cannot go in the past more than active days"
              + " in entity for column: "
              + column.getId());
    }

    return new SlidingWindowColumn(column,
        columns.get(dto.getSourceProperty()),
        new RelativeDaysInterval(dto.getRelativeDaysFrom(), dto.getRelativeDaysTo()),
        WindowAggregationFunction.valueOf(
            dto.getWindowFunction().name()));
  }
}
