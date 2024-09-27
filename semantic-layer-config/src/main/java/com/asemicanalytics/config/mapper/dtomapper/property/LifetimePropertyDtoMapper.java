package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.LifetimeColumn;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyLifetimeDto;
import java.util.function.Function;

public class LifetimePropertyDtoMapper implements
    Function<EntityPropertyLifetimeDto, LifetimeColumn> {
  private final Column column;
  private final EventLogicalTables eventLogicalTables;

  public LifetimePropertyDtoMapper(Column column,
                                   EventLogicalTables eventLogicalTables) {
    this.column = column;
    this.eventLogicalTables = eventLogicalTables;
  }

  @Override
  public LifetimeColumn apply(
      EntityPropertyLifetimeDto dto) {
    EntityProperty sourceColumn = ComposableColumnHelper.getSourceColumn(
        column,
        dto.getSourceProperty(),
        dto.getSourceComputedProperty(),
        dto.getSourceEventProperty(),
        eventLogicalTables
    );

    return new LifetimeColumn(
        column,
        sourceColumn,
        LifetimeColumn.MergeFunction.valueOf(dto.getMergeFunction().name()));
  }
}
