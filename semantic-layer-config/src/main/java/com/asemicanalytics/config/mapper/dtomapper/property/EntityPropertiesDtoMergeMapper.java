package com.asemicanalytics.config.mapper.dtomapper.property;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.PropertiesDto;
import java.util.List;
import java.util.function.Function;

public class EntityPropertiesDtoMergeMapper implements
    Function<List<EntityPropertiesDto>, EntityPropertiesDto> {

  private void merge(EntityPropertiesDto columns, EntityPropertiesDto toMerge) {
    for (var entry : toMerge.getProperties().getAdditionalProperties().entrySet()) {
      if (columns.getProperties().getAdditionalProperties()
          .put(entry.getKey(), entry.getValue()) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + entry.getKey() + " in entity");
      }
    }
  }

  @Override
  public EntityPropertiesDto apply(List<EntityPropertiesDto> columns) {

    EntityPropertiesDto result = new EntityPropertiesDto();
    result.setProperties(new PropertiesDto());
    for (EntityPropertiesDto column : columns) {
      merge(result, column);
    }

    // TODO check for duplicates in labels
    return result;
  }
}
