package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.FixedWindowColumn;
import com.asemicanalytics.core.logicaltable.entity.WindowAggregationFunction;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFixedWindowDto;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

public class FixedWindowPropertyDtoMapper implements
    Function<EntityPropertyFixedWindowDto, FixedWindowColumn> {

  private final Column column;
  private final Map<String, EventLogicalTable> actionLogicalTables;


  public FixedWindowPropertyDtoMapper(Column column,
                                      Map<String, EventLogicalTable> actionLogicalTables) {
    this.column = column;
    this.actionLogicalTables = actionLogicalTables;
  }

  @Override
  public FixedWindowColumn apply(
      EntityPropertyFixedWindowDto dto) {

    EntityProperty sourceColumn = ComposableColumnHelper.getSourceColumn(
        column,
        dto.getSourceProperty(),
        dto.getSourceComputedProperty(),
        dto.getSourceActionProperty(),
        actionLogicalTables
    );

    return new FixedWindowColumn(column,
        sourceColumn,
        new DateInterval(
            LocalDate.parse(dto.getDateFrom()),
            LocalDate.parse(dto.getDateTo())),
        WindowAggregationFunction.valueOf(
            dto.getWindowFunction().name()));
  }
}
