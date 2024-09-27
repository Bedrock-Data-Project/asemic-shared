package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyEventDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComposableColumnHelper {
  public static EntityProperty getSourceColumn(
      Column column,
      Optional<String> sourceProperty,
      Optional<EntityPropertyComputedDto> computedSourceProperty,
      Optional<EntityPropertyEventDto> eventSourceProperty,
      Map<String, EventLogicalTable> actionLogicalTables
  ) {
    EntityProperty sourceColumn = null;

    var innerColumn = Column.ofHidden(column.getId() + "__inner", column.getDataType());
    if (sourceProperty.isPresent()) {
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(
              new EntityPropertyComputedDto("{" + sourceProperty.get() + "}", List.of()));
    }

    if (computedSourceProperty.isPresent()) {
      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(computedSourceProperty.get());
    }

    if (eventSourceProperty.isPresent()) {
      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new EventPropertyDtoMapper(innerColumn, actionLogicalTables, true)
          .apply(eventSourceProperty.get());
    }

    if (sourceColumn == null) {
      throw new IllegalArgumentException(
          "Must have either source property, source action property or source computed property");
    }

    return sourceColumn;
  }
}
