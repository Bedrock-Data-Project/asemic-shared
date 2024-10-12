package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.entity.EntityProperty;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyEventDto;
import java.util.List;
import java.util.Optional;

public class ComposableColumnHelper {
  public static EntityProperty getSourceColumn(
      Column column,
      Optional<String> sourceProperty,
      Optional<EntityPropertyComputedDto> computedSourceProperty,
      Optional<EntityPropertyEventDto> eventSourceProperty,
      EventLogicalTables eventLogicalTables
  ) {
    EntityProperty sourceColumn = null;

    if (sourceProperty.isPresent()) {
      var innerColumn = Column.ofHidden(column.getId() + "__inner", column.getDataType());
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(
              new EntityPropertyComputedDto("{" + sourceProperty.get() + "}",
                  List.of(), null));
    }

    if (computedSourceProperty.isPresent()) {
      var innerColumn = Column.ofHidden(column.getId() + "__inner",
          computedSourceProperty.get().getDataType()
              .map(selectDataType -> DataType.valueOf(selectDataType.name()))
              .orElse(column.getDataType()));

      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new ComputedPropertyDtoMapper(innerColumn)
          .apply(computedSourceProperty.get());
    }

    if (eventSourceProperty.isPresent()) {
      var innerColumn = Column.ofHidden(column.getId() + "__inner",
          eventSourceProperty.get().getDataType()
              .map(selectDataType -> DataType.valueOf(selectDataType.name()))
              .orElse(column.getDataType()));
      if (sourceColumn != null) {
        throw new IllegalArgumentException(
            "Can have either source property, source action property or source computed property");
      }
      sourceColumn = new EventPropertyDtoMapper(innerColumn, eventLogicalTables, true)
          .apply(eventSourceProperty.get());
    }

    if (sourceColumn == null) {
      throw new IllegalArgumentException(
          "Must have either source property, source action property or source computed property");
    }

    return sourceColumn;
  }
}
