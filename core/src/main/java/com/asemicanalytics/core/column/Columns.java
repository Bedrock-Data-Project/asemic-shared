package com.asemicanalytics.core.column;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.function.Predicate;

public class Columns<T extends Column> implements Iterable<T> {
  private final SequencedMap<String, T> columns;

  public Columns(SequencedMap<String, T> columns) {
    this.columns = Collections.unmodifiableSequencedMap(columns);

    if (columns.isEmpty()) {
      throw new IllegalArgumentException("Must have at least one column");
    }
  }

  public SequencedMap<String, T> getColumns() {
    return columns;
  }

  public T column(String id) {
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

  public List<T> getColumnsByTag(String tag) {
    return columns.values().stream()
        .filter(c -> c.hasTag(tag))
        .toList();
  }

  public String getColumnIdByTag(String tag) {
    return getColumnIdByTagIfExists(tag)
        .orElseThrow(() ->
            new IllegalArgumentException("No column with tag " + tag));
  }

  public Columns<T> filter(Predicate<T> predicate) {
    return new Columns<>(columns.entrySet().stream()
        .filter(entry -> predicate.test(entry.getValue()))
        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()),
            LinkedHashMap::putAll));
  }

  @Override
  public Iterator<T> iterator() {
    return columns.values().iterator();
  }

  public Columns<T> add(Columns<T> columns) {
    SequencedMap<String, T> merged = new LinkedHashMap<>(this.columns);
    merged.putAll(columns.columns);
    return new Columns(merged);
  }
}
