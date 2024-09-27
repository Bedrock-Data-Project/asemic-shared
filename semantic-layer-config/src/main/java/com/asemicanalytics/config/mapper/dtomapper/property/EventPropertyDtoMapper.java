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
        dto.getWhere(), dto.getSelect(),
        EventColumn.AggregateFunction.valueOf(dto.getAggregateFunction().name()),
        dto.getDefaultValue().orElse(null),
        generated);
  }
}
