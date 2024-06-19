package com.asemicanalytics.core.column;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.function.Predicate;

public class Columns implements Iterable<Column> {
  private final SequencedMap<String, Column> columns;

  public Columns(SequencedMap<String, Column> columns) {
    this.columns = Collections.unmodifiableSequencedMap(columns);

    if (columns.isEmpty()) {
      throw new IllegalArgumentException("Must have at least one column");
    }
  }

  public SequencedMap<String, Column> getColumns() {
    return columns;
  }

  public Column column(String id) {
    if (!hasColumn(id)) {
      throw new IllegalArgumentException("No column named " + id);
    }
    return columns.get(id);
  }

  public boolean hasColumn(String id) {
    return columns.containsKey(id);
  }

  public Optional<String> getColumnIdByTagIfExists(String tag) {
    return columns.entrySet().stream()
        .filter(entry -> entry.getValue().hasTag(tag))
        .map(Map.Entry::getKey)
        .findFirst();
  }

  public String getColumnIdByTag(String tag) {
    return getColumnIdByTagIfExists(tag)
        .orElseThrow(() -> new IllegalArgumentException("No column with tag " + tag));
  }

  public Columns filter(Predicate<Column> predicate) {
    return new Columns(columns.entrySet().stream()
        .filter(entry -> predicate.test(entry.getValue()))
        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()),
            LinkedHashMap::putAll));
  }

  @Override
  public Iterator<Column> iterator() {
    return columns.values().iterator();
  }
}
