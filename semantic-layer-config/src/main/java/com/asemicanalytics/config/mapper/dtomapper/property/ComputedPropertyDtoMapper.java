package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.ComputedColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import java.util.function.Function;

public class ComputedPropertyDtoMapper implements
    Function<EntityPropertyComputedDto, ComputedColumn> {
  private final Column column;

  public ComputedPropertyDtoMapper(Column column) {
    this.column = column;
  }

  @Override
  public ComputedColumn apply(
      EntityPropertyComputedDto dto) {
    return new ComputedColumn(column, dto.getSelect());
  }
}
