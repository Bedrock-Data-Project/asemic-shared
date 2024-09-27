package com.asemicanalytics.config.mapper.dtomapper.event;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EventColumnDto;
import java.util.HashSet;
import java.util.function.Function;

public class EventColumnDtoMapper implements Function<EventColumnDto, Column> {
  private final String id;

  public EventColumnDtoMapper(String id) {
    this.id = id;
  }

  @Override
  public Column apply(EventColumnDto dto) {
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
