package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import java.util.Map;
import java.util.Set;

public abstract class EntityProperty extends Column {
  public EntityProperty(Column column) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
  }

  public abstract EntityPropertyType getType();

  public abstract Set<String> referencedProperties();

  public abstract Map<EventLogicalTable, Set<String>> referencedEventParameters();
}
