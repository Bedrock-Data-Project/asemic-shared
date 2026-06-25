package com.asemicanalytics.core.logicaltable.event;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ActivityLogicalTable extends EventLogicalTable {
  public static final String TAG = "activity_event";
  private final List<EventLogicalTable> activityEvents;

  public ActivityLogicalTable(TableReference table,
                              List<EventLogicalTable> activityEvents) {
    super("entity_activity", "Activity", Optional.empty(), table,
        new Columns<Column>(new LinkedHashMap<>(Map.of(
            ASEMIC_ENTITY_ID,
            activityEvents.getFirst().entityIdColumn().withId(ASEMIC_ENTITY_ID),
            ASEMIC_EVENT_TIMESTAMP,
            activityEvents.getFirst().getTimestampColumn().withId(ASEMIC_EVENT_TIMESTAMP),
            DERIVED_DATE_COLUMN_ID,
            activityEvents.getFirst().getDateColumn().withId(DERIVED_DATE_COLUMN_ID)
        ))),
        Map.of(),
        Optional.empty(), Set.of());
    this.activityEvents = activityEvents;
  }

  public List<EventLogicalTable> getActivityEvents() {
    return activityEvents;
  }
}
