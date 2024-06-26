package com.asemicanalytics.config.mapper.dtomapper.entity;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class EntityPropertiesDtoMergeMapper implements
    Function<List<EntityPropertiesDto>, EntityPropertiesDto> {

  private void merge(EntityPropertiesDto columns, EntityPropertiesDto toMerge) {
    columns.getFirstAppearanceProperties()
        .ifPresent(cols -> cols.addAll(toMerge.getFirstAppearanceProperties().orElse(List.of())));
    columns.getActionProperties()
        .ifPresent(cols -> cols.addAll(toMerge.getActionProperties().orElse(List.of())));
    columns.getTotalProperties()
        .ifPresent(cols -> cols.addAll(toMerge.getTotalProperties().orElse(List.of())));
    columns.getComputedProperties()
        .ifPresent(cols -> cols.addAll(toMerge.getComputedProperties().orElse(List.of())));
  }

  @Override
  public EntityPropertiesDto apply(List<EntityPropertiesDto> columns) {

    EntityPropertiesDto result = new EntityPropertiesDto();
    for (EntityPropertiesDto column : columns) {
      merge(result, column);
    }

    Set<String> columnIds = new HashSet<>();
    result.getFirstAppearanceProperties().orElse(List.of()).forEach(col -> {
      if (!columnIds.add(col.getColumn().getId())) {
        throw new IllegalArgumentException(
            "Duplicate column id: " + col.getColumn().getId() + " in entity");
      }
    });
    return result;
  }
}
