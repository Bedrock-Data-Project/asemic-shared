package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.entity.LifetimeColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyLifetimeDto;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LifetimePropertyDtoMapper implements
    Function<EntityPropertyLifetimeDto, LifetimeColumn> {
  private final Column column;
  private final Map<String, ActionLogicalTable> actionLogicalTables;

  public LifetimePropertyDtoMapper(Column column,
                                   Map<String, ActionLogicalTable> actionLogicalTables) {
    this.column = column;
    this.actionLogicalTables = actionLogicalTables;
  }

  @Override
  public LifetimeColumn apply(
      EntityPropertyLifetimeDto dto) {
    EntityProperty sourceColumn = null;

    var innerColumn = Column.ofHidden(column.getId() + "__inner", column.getDataType());
    if (dto.getSourceProperty().isPresent()) {
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(
              new EntityPropertyComputedDto("{" + dto.getSourceProperty().get() + "}", List.of()));
    }

    if (dto.getSourceComputedProperty().isPresent()) {
      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(dto.getSourceComputedProperty().get());
    }

    if (dto.getSourceActionProperty().isPresent()) {
      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new ActionPropertyDtoMapper(innerColumn, actionLogicalTables)
          .apply(dto.getSourceActionProperty().get());
    }

    if (sourceColumn == null) {
      throw new IllegalArgumentException(
          "Must have either source property, source action property or source computed property");
    }

    return new LifetimeColumn(
        column,
        sourceColumn,
        LifetimeColumn.MergeFunction.valueOf(dto.getMergeFunction().name()));
  }
}
