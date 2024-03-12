package com.asemicanalytics.core;

import java.util.Optional;

public class Column {
  protected final String id;
  protected final DataType dataType;
  protected final String label;
  protected final Optional<String> description;
  protected final boolean canFilter;
  protected final boolean canGroupBy;

  public Column(String id, DataType dataType, String label, Optional<String> description,
                boolean canFilter, boolean canGroupBy) {
    this.id = id;
    this.dataType = dataType;
    this.label = label;
    this.description = description;
    this.canFilter = canFilter;
    this.canGroupBy = canGroupBy;
  }

  public static Column of(String id, DataType dataType, boolean canFilter, boolean canGroupBy) {
    return new Column(id, dataType, id.replace("_", " "), Optional.empty(), canFilter, canGroupBy);
  }

  public static Column ofHidden(String id, DataType dataType) {
    return new Column(id, dataType, id.replace("_", " "), Optional.empty(), false, false);
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
}
