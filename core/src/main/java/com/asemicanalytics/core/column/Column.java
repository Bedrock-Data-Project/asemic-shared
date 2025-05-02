package com.asemicanalytics.core.column;

import com.asemicanalytics.core.DataType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Column {
  protected final String id;
  protected final DataType dataType;
  protected final String label;
  protected final Optional<String> description;
  protected final boolean canFilter;
  protected final boolean canGroupBy;
  protected Set<String> tags;

  public Column(String id, DataType dataType, String label, Optional<String> description,
                boolean canFilter, boolean canGroupBy, Set<String> tags) {
    this.id = id;
    this.dataType = dataType;
    this.label = label;
    this.description = description;
    this.canFilter = canFilter;
    this.canGroupBy = canGroupBy;
    this.tags = Collections.unmodifiableSet(tags);
  }

  public static Column of(String id, DataType dataType, boolean canFilter, boolean canGroupBy) {
    return new Column(id, dataType, id.replace("_", " "),
        Optional.empty(), canFilter, canGroupBy, Set.of());
  }

  public static Column ofHidden(String id, DataType dataType) {
    return new Column(id, dataType, id.replace("_", " "),
        Optional.empty(), false, false, Set.of());
  }

  public Column withTag(String tag) {
    var tags = new HashSet<>(this.tags);
    tags.add(tag);
    return new Column(id, dataType, label, description, canFilter, canGroupBy,
        tags);
  }

  public String getId() {
    return id;
  }

  public DataType getDataType() {
    return dataType;
  }

  public String getLabel() {
    return label;
  }

  public Optional<String> getDescription() {
    return description;
  }

  public boolean canFilter() {
    return canFilter;
  }

  public boolean canGroupBy() {
    return canGroupBy;
  }

  public Set<String> getTags() {
    return tags;
  }

  public boolean hasTag(String tag) {
    return tags.contains(tag);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Column column = (Column) o;
    return Objects.equals(id, column.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
