package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;

public abstract class EntityProperty extends Column {
  public EntityProperty(Column column) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
  }

  public abstract EntityPropertyType getType();
}
