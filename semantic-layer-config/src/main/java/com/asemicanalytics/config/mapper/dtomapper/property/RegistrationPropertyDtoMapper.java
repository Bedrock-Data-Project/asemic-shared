package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.RegistrationColumn;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyRegistrationDto;
import java.util.function.Function;

public class RegistrationPropertyDtoMapper implements
    Function<EntityPropertyRegistrationDto, RegistrationColumn> {

  private final RegistrationsLogicalTable registrationsLogicalTable;
  private final Column column;


  public RegistrationPropertyDtoMapper(RegistrationsLogicalTable registrationsLogicalTable,
                                       Column column) {
    this.registrationsLogicalTable = registrationsLogicalTable;
    this.column = column;
  }

  @Override
  public RegistrationColumn apply(
      EntityPropertyRegistrationDto dto) {
    return new RegistrationColumn(column,
        registrationsLogicalTable,
        dto.getSourceColumn().orElse(column.getId()));
  }
}
