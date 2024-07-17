package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import java.util.Map;
import java.util.function.Function;

public class ActionPropertyDtoMapper implements
    Function<EntityPropertyActionDto, ActionColumn> {

  private final Map<String, ActionLogicalTable> actionLogicalTables;

  public ActionPropertyDtoMapper(Map<String, ActionLogicalTable> actionLogicalTables) {
    this.actionLogicalTables = actionLogicalTables;
  }

  @Override
  public ActionColumn apply(
      EntityPropertyActionDto dto) {
    var logicalTable = actionLogicalTables.get(dto.getActionLogicalTable());
    if (logicalTable == null) {
      throw new IllegalArgumentException("Action logical table not found: "
          + dto.getActionLogicalTable()
          + " in entity for column: "
          + dto.getColumn().getId());
    }

    return new ActionColumn(new ColumnDtoMapper().apply(dto.getColumn()),
        logicalTable,
        dto.getWhere(), dto.getSelect(),
        dto.getMissingValue(),
        dto.getCanMaterialize().orElse(true));
  }
}
