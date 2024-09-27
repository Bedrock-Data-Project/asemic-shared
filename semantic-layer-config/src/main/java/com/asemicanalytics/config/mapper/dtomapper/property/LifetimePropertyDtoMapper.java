package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.LifetimeColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyLifetimeDto;
import java.util.Map;
import java.util.function.Function;

public class LifetimePropertyDtoMapper implements
    Function<EntityPropertyLifetimeDto, LifetimeColumn> {
  private final Column column;
  private final Map<String, EventLogicalTable> actionLogicalTables;

  public LifetimePropertyDtoMapper(Column column,
                                   Map<String, EventLogicalTable> actionLogicalTables) {
    this.column = column;
    this.actionLogicalTables = actionLogicalTables;
  }

  @Override
  public LifetimeColumn apply(
      EntityPropertyLifetimeDto dto) {
    EntityProperty sourceColumn = ComposableColumnHelper.getSourceColumn(
        column,
        dto.getSourceProperty(),
        dto.getSourceComputedProperty(),
        dto.getSourceActionProperty(),
        actionLogicalTables
    );

    return new LifetimeColumn(
        column,
        sourceColumn,
        LifetimeColumn.MergeFunction.valueOf(dto.getMergeFunction().name()));
  }
}
