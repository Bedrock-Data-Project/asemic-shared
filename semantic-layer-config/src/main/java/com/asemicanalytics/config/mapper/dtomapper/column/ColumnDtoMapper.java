package com.asemicanalytics.config.mapper.dtomapper.column;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnDto;
import java.util.HashSet;
import java.util.function.Function;

public class ColumnDtoMapper implements Function<ColumnDto, Column> {
  @Override
  public Column apply(ColumnDto dto) {
    return new Column(
        dto.getId(),
        DataType.valueOf(dto.getDataType().value()),
        DefaultLabel.of(dto.getLabel(), dto.getId()),
        dto.getDescription(),
        dto.getCanFilter().orElse(true),
        dto.getCanGroupBy().orElse(false),
        dto.getTags().map(t -> new HashSet<>(t)).orElse(new HashSet<>())
    );
  }
}
