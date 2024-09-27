package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.RegistrationColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyRegistrationDto;
import java.util.function.Function;

public class RegistrationPropertyDtoMapper implements
    Function<EntityPropertyRegistrationDto, RegistrationColumn> {

  private final Column column;

  public RegistrationPropertyDtoMapper(Column column) {
    this.column = column;
  }

  @Override
  public RegistrationColumn apply(
      EntityPropertyRegistrationDto dto) {
    return new RegistrationColumn(column, dto.getSourceColumn().orElse(column.getId()));
  }
}
