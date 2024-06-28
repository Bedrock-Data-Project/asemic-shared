package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.logicaltable.entity.SlidingWindowColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

public class SlidingWindowPropertyDtoMapper implements
    Function<EntityPropertySlidingWindowDto, SlidingWindowColumn> {


  @Override
  public SlidingWindowColumn apply(
      EntityPropertySlidingWindowDto dto) {

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

    return new SlidingWindowColumn(new ColumnDtoMapper().apply(dto.getColumn()),
        dto.getSourceProperty(),
        dto.getRelativeDaysFrom(), dto.getRelativeDaysTo(),
        dto.getFunction().value(),
        Optional.of(LocalDate.MIN));
  }
}
