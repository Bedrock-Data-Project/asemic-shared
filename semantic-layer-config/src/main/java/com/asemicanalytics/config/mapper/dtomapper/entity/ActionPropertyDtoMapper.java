package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.ActionColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

// TODO WINDOW!
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

    var window = dto.getWindow().orElse(List.of(0, 0));
    if (window.isEmpty()) {
      window.add(0);
      window.add(0);
    }

    if (window.size() != 2) {
      throw new IllegalArgumentException("window must have 2 values"
          + " in entity for column: "
          + dto.getColumn().getId());
    }
    if (window.get(0) > 0 || window.get(1) > 0) {
      throw new IllegalArgumentException("window days must 0 or negative"
          + " in entity for column: "
          + dto.getColumn().getId());
    }
    var windowAggregation = dto.getWindowAggregation().orElse("MAX");
    if (!Set.of("MAX", "MIN", "SUM", "AVG", "COUNT")
        .contains(windowAggregation.toUpperCase())) {
      throw new IllegalArgumentException(
          "Invalid window aggregation: " + windowAggregation
              + " in entity for column: "
              + dto.getColumn().getId());
    }

    return new ActionColumn(new ColumnDtoMapper().apply(dto.getColumn()),
        logicalTable,
        dto.getWhere(), dto.getAggregationExpression(),
        Optional.of(LocalDate.MIN), dto.getMissingValue(),
        window.get(0), window.get(1), windowAggregation,
        dto.getCanMaterialize().orElse(true));
  }
}
