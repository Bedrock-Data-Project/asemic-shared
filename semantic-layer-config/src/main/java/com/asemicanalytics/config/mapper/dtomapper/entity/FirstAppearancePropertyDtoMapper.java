package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.logicaltable.entity.FirstAppearanceColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFirstAppearanceDto;
import java.util.function.Function;

public class FirstAppearancePropertyDtoMapper implements
    Function<EntityPropertyFirstAppearanceDto, FirstAppearanceColumn> {

  @Override
  public FirstAppearanceColumn apply(
      EntityPropertyFirstAppearanceDto dto) {
    return new FirstAppearanceColumn(
        new ColumnDtoMapper().apply(dto.getColumn()),
        dto.getSourceColumn().orElse(dto.getColumn().getId()));
  }
}
