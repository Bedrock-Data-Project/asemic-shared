package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.logicaltable.entity.TotalColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyTotalDto;
import java.util.function.Function;

public class TotalPropertyDtoMapper implements
    Function<EntityPropertyTotalDto, TotalColumn> {

  @Override
  public TotalColumn apply(
      EntityPropertyTotalDto dto) {
    return new TotalColumn(
        new ColumnDtoMapper().apply(dto.getColumn()),
        dto.getSourceProperty(),
        dto.getFunction().value());
  }
}
