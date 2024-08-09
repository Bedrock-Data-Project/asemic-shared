package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.ComputedColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import java.util.ArrayList;
import java.util.List;
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

    List<ComputedColumn.ValueMapping> valueMappings = new ArrayList<>();
    for (var valueMapping : dto.getValueMappings().orElse(List.of())) {
      if (valueMapping.getConstant().isPresent()) {
        valueMappings.add(new ComputedColumn.ValueMapping(
            valueMapping.getConstant(),
            valueMapping.getConstant(),
            valueMapping.getNewValue().get().toString()
        ));
        continue;
      }

      if (valueMapping.getRange().isPresent()) {
        valueMappings.add(new ComputedColumn.ValueMapping(
            valueMapping.getRange().get().getFrom().map(String::valueOf),
            valueMapping.getRange().get().getTo().map(String::valueOf),
            valueMapping.getNewValue().get().toString()
        ));
        continue;
      }

      throw new IllegalArgumentException("Value mapping must have either constant or range");
    }

    return new ComputedColumn(column, dto.getSelect(), valueMappings);
  }
}
