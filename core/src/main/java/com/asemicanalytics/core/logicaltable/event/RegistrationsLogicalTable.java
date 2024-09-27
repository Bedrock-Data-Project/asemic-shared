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
  public static final String TAG = "registrations_event";
  public static final String REGISTRATIONS_PROPERTY_TAG = "registrations_property";

  private List<EventLogicalTable> registrationEvents;

  public RegistrationsLogicalTable(String id,
                                   TableReference table,
                                   List<EventLogicalTable> registrationEvents) {
    super(id, null, Optional.empty(), table,
        new Columns<Column>(new LinkedHashMap<>(Map.of(
            registrationEvents.getFirst().getEntityIdColumnId(),
            registrationEvents.getFirst().entityIdColumn(),
            registrationEvents.getFirst().getTimestampColumnId(),
            registrationEvents.getFirst().getTimestampColumn(),
            registrationEvents.getFirst().getDateColumnId(),
            registrationEvents.getFirst().getDateColumn()
        ))),
        Map.of(),
        Optional.empty(), Set.of());
    this.registrationEvents = registrationEvents;
  }

  public List<EventLogicalTable> getRegistrationEvents() {
    return registrationEvents;
  }
}
