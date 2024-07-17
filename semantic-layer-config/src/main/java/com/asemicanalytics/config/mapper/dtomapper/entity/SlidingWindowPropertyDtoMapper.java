package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.core.logicaltable.entity.MaterializedColumnRepository;
import com.asemicanalytics.core.logicaltable.entity.SlidingWindowColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SlidingWindowPropertyDtoMapper implements
    Function<EntityPropertySlidingWindowDto, SlidingWindowColumn> {

  private final Map<String, Column> columns;
  private final int activeDays;

  public SlidingWindowPropertyDtoMapper(Map<String, Column> columns, int activeDays) {
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
          + dto.getColumn().getId());
    }

    if (!(columns.get(dto.getSourceProperty()) instanceof ActionColumn)) {
      throw new IllegalArgumentException("Source property must be an action column: "
          + dto.getSourceProperty()
          + " in entity for column: "
          + dto.getColumn().getId());
    }

    if (dto.getRelativeDaysFrom() > 0 || dto.getRelativeDaysTo() > 0) {
      throw new IllegalArgumentException("window days must be 0 or negative"
          + " in entity for column: "
          + dto.getColumn().getId());
    }

    if (dto.getRelativeDaysFrom() > dto.getRelativeDaysTo()) {
      throw new IllegalArgumentException(
          "relative days from must be less than or equal to relative days to"
              + " in entity for column: "
              + dto.getColumn().getId());
    }

    if (dto.getRelativeDaysTo() < -activeDays) {
      throw new IllegalArgumentException(
          "window cannot go in the past more than active days"
              + " in entity for column: "
              + dto.getColumn().getId());
    }

    return new SlidingWindowColumn(new ColumnDtoMapper().apply(dto.getColumn()),
        (ActionColumn) columns.get(dto.getSourceProperty()),
        dto.getRelativeDaysFrom(), dto.getRelativeDaysTo(),
        dto.getFunction().value());
  }
}
