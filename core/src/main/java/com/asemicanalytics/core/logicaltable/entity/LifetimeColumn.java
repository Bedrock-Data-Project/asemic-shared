package com.asemicanalytics.core.logicaltable.entity;


import com.asemicanalytics.core.column.Column;

public class LifetimeColumn extends EntityProperty {

  private final EntityProperty sourceColumn;
  private final MergeFunction mergeFunction;

  @Override
  public EntityPropertyType getType() {
    return EntityPropertyType.LIFETIME;
  }

  public enum MergeFunction {
    SUM,
    MIN,
    MAX,
    FIRST_VALUE,
    LAST_VALUE,
  }

  public LifetimeColumn(Column column, EntityProperty sourceColumn, MergeFunction mergeFunction) {
    super(column);
    this.sourceColumn = sourceColumn;
    this.mergeFunction = mergeFunction;
  }

  public EntityProperty getSourceColumn() {
    return sourceColumn;
  }

  public MergeFunction getMergeFunction() {
    return mergeFunction;
  }
}
