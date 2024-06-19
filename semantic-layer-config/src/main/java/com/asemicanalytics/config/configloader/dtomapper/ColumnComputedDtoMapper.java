package com.asemicanalytics.config.configloader.dtomapper;


import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnComputedDto;
import java.util.function.Function;

public class ColumnComputedDtoMapper implements Function<ColumnComputedDto, ComputedColumn> {
  @Override
  public ComputedColumn apply(ColumnComputedDto dto) {
    return new ComputedColumn(
        new ColumnDtoMapper().apply(dto.getColumn()),
        dto.getFormula());
  }
}
