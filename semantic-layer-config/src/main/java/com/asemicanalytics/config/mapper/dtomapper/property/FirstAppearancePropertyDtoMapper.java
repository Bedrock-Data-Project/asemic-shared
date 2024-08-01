package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.FirstAppearanceColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFirstAppearanceDto;
import java.util.function.Function;

public class FirstAppearancePropertyDtoMapper implements
    Function<EntityPropertyFirstAppearanceDto, FirstAppearanceColumn> {

  private final Column column;

  public FirstAppearancePropertyDtoMapper(Column column) {
    this.column = column;
  }

  @Override
  public FirstAppearanceColumn apply(
      EntityPropertyFirstAppearanceDto dto) {
    return new FirstAppearanceColumn(column, dto.getSourceColumn().orElse(column.getId()));
  }
}
