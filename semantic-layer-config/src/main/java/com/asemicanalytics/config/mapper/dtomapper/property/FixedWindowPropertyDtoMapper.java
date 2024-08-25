package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.FixedWindowColumn;
import com.asemicanalytics.core.logicaltable.entity.WindowAggregationFunction;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFixedWindowDto;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

public class FixedWindowPropertyDtoMapper implements
    Function<EntityPropertyFixedWindowDto, FixedWindowColumn> {

  private final Map<String, EntityProperty> columns;
  private final Column column;

  public FixedWindowPropertyDtoMapper(Column column, Map<String, EntityProperty> columns) {
    this.column = column;
    this.columns = columns;
  }

  @Override
  public FixedWindowColumn apply(
      EntityPropertyFixedWindowDto dto) {

    if (!columns.containsKey(dto.getSourceProperty())) {
      throw new IllegalArgumentException("Source property not found: "
          + dto.getSourceProperty()
          + " in entity for column: "
          + column.getId());
    }

    return new FixedWindowColumn(column,
        columns.get(dto.getSourceProperty()),
        new DateInterval(
            LocalDate.parse(dto.getDateFrom()),
            LocalDate.parse(dto.getDateTo())),
        WindowAggregationFunction.valueOf(
            dto.getWindowFunction().name()));
  }
}
