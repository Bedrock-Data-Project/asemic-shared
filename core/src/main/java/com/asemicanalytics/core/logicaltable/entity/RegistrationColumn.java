package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import java.util.Map;
import java.util.Set;

public class RegistrationColumn extends EntityProperty {
  private final RegistrationsLogicalTable registrationLogicalTable;
  private final String registrationTableColumn;

  public RegistrationColumn(Column column, RegistrationsLogicalTable registrationLogicalTable,
                            String firstAppearanceTableColumn) {
    super(column);
    this.registrationLogicalTable = registrationLogicalTable;
    this.registrationTableColumn = firstAppearanceTableColumn;
  }

  public String getRegistrationTableColumn() {
    return registrationTableColumn;
  }

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.REGISTRATION;
  }

  @Override
  public Set<String> referencedProperties() {
    return Set.of();
  }

  @Override
  public Map<EventLogicalTable, Set<String>> referencedEventParameters() {
    return Map.of(registrationLogicalTable, Set.of(registrationTableColumn));
  }
}
