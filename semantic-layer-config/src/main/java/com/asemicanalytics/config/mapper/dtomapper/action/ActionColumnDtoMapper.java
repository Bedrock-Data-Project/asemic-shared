package com.asemicanalytics.config.mapper.dtomapper.action;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionColumnDto;
import java.util.HashSet;
import java.util.function.Function;

public class ActionColumnDtoMapper implements Function<ActionColumnDto, Column> {
  private final String id;

  public ActionColumnDtoMapper(String id) {
    this.id = id;
  }

  @Override
  public Column apply(ActionColumnDto dto) {
    return new Column(
        id,
        DataType.valueOf(dto.getDataType().name()),
        DefaultLabel.of(dto.getLabel(), id),
        dto.getDescription(),
        true,
        true,
        dto.getTags().map(HashSet::new).orElse(new HashSet<>())
    );
  }
}
