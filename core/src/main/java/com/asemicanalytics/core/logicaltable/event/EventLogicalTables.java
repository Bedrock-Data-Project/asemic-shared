package com.asemicanalytics.core.logicaltable.event;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventLogicalTables implements Iterable<EventLogicalTable> {
  private final Map<String, EventLogicalTable> eventLogicalTables;

  public EventLogicalTables(Map<String, EventLogicalTable> eventLogicalTables) {
    this.eventLogicalTables = eventLogicalTables;
  }

  public EventLogicalTable get(String id) {
    if (!eventLogicalTables.containsKey(id)) {
      throw new IllegalArgumentException("No event logical table with id " + id);
    }
    return eventLogicalTables.get(id);
  }

  public List<EventLogicalTable> getByTag(String tag) {
    return eventLogicalTables.values().stream()
        .filter(eventLogicalTable -> eventLogicalTable.getTags().contains(tag))
        .collect(Collectors.toList());
  }

  @Override
  public Iterator<EventLogicalTable> iterator() {
    return eventLogicalTables.values().iterator();
  }

  public Map<String, EventLogicalTable> getEventLogicalTables() {
    return eventLogicalTables;
  }
}
