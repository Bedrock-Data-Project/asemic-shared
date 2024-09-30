package com.asemicanalytics.core.logicaltable.event;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RegistrationsLogicalTable extends EventLogicalTable {
  public static final String TAG = "registration_event";
  public static final String REGISTRATIONS_PROPERTY_TAG = "registration_property";
  private List<EventLogicalTable> registrationEvents;

  private static Columns<Column> buildColumns(List<EventLogicalTable> registrationEvents) {
    LinkedHashMap<String, Column> columns = new LinkedHashMap<>();


    columns.put(registrationEvents.getFirst().getEntityIdColumnId(),
        registrationEvents.getFirst().entityIdColumn());
    columns.put(registrationEvents.getFirst().getTimestampColumnId(),
        registrationEvents.getFirst().getTimestampColumn());
    columns.put(registrationEvents.getFirst().getDateColumnId(),
        registrationEvents.getFirst().getDateColumn());

    for (var eventTable : registrationEvents) {
      for (var column : eventTable.getColumns()) {
        if (column.hasTag(RegistrationsLogicalTable.REGISTRATIONS_PROPERTY_TAG)) {
          columns.put(column.getId(), column);
        }
      }
    }

    return new Columns<>(columns);
  }

  public RegistrationsLogicalTable(TableReference table,
                                   List<EventLogicalTable> registrationEvents) {
    super("entity_registrations", "Registrations", Optional.empty(), table,
        buildColumns(registrationEvents),
        Map.of(),
        Optional.empty(), Set.of());
    this.registrationEvents = registrationEvents;
  }

  public List<EventLogicalTable> getRegistrationEvents() {
    return registrationEvents;
  }
}
