package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import java.util.Map;
import java.util.function.Function;

public class ActionPropertyDtoMapper implements
    Function<EntityPropertyActionDto, ActionColumn> {

  private final Column column;
  private final Map<String, EventLogicalTable> actionLogicalTables;
  private final boolean generated;

  public ActionPropertyDtoMapper(Column column,
                                 Map<String, EventLogicalTable> actionLogicalTables,
                                 boolean generated) {
    this.column = column;
    this.actionLogicalTables = actionLogicalTables;
    this.generated = generated;
  }

  @Override
  public ActionColumn apply(
      EntityPropertyActionDto dto) {
    var logicalTable = actionLogicalTables.get(dto.getSourceAction());
    if (logicalTable == null) {
      throw new IllegalArgumentException("Action not found: "
          + dto.getSourceAction()
          + " in entity for column: "
          + column.getId());
    }

    return new ActionColumn(column,
        logicalTable,
        dto.getWhere(), dto.getSelect(),
        ActionColumn.AggregateFunction.valueOf(dto.getAggregateFunction().name()),
        dto.getDefaultValue().orElse(null),
        generated);
  }
}
