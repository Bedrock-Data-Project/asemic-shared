package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EventColumn;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyEventDto;
import java.util.function.Function;

public class EventPropertyDtoMapper implements
    Function<EntityPropertyEventDto, EventColumn> {

  private final Column column;
  private final EventLogicalTables eventLogicalTables;
  private final boolean generated;

  public EventPropertyDtoMapper(Column column,
                                EventLogicalTables eventLogicalTables,
                                boolean generated) {
    this.column = column;
    this.eventLogicalTables = eventLogicalTables;
    this.generated = generated;
  }

  @Override
  public EventColumn apply(
      EntityPropertyEventDto dto) {
    var logicalTable = eventLogicalTables.get(dto.getSourceEvent());
    if (logicalTable == null) {
      throw new IllegalArgumentException("Action not found: "
          + dto.getSourceEvent()
          + " in entity for column: "
          + column.getId());
    }

    return new EventColumn(column,
        logicalTable,
        // select is a YAML scalar (select: 1 is valid) -> Object in the DTO
        dto.getWhere(), String.valueOf(dto.getSelect()),
        EventColumn.AggregateFunction.valueOf(dto.getAggregateFunction().name()),
        // default_value is a YAML scalar (default 0 is common) -> Object in the DTO
        dto.getDefaultValue().map(String::valueOf).orElse(null),
        generated);
  }
}
