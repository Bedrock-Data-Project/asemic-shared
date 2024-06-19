package com.asemicanalytics.core.datasource.userwide;

import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import java.time.LocalDate;
import java.util.Optional;

public class UserActionColumn extends Column {
  private final UserActionDatasource userActionDatasource;
  private final Optional<String> where;
  private final String aggregationTableExpression;
  private final Optional<LocalDate> materializedFrom;
  private final String missingValue;
  private final int relativeDaysFrom;
  private final int relativeDaysTo;
  private final String windowAggregation;
  private final boolean canMaterialize;

  public UserActionColumn(
      Column column,
      UserActionDatasource userActionDatasource,
      Optional<String> where,
      String aggregationTableExpression,
      Optional<LocalDate> materializedFrom,
      String missingValue,
      int relativeDaysFrom,
      int relativeDaysTo,
      String windowAggregation,
      boolean canMaterialize) {
    super(column.getId(), column.getDataType(), column.getLabel(), column.getDescription(),
        column.canFilter(), column.canGroupBy(), column.getTags());
    this.userActionDatasource = userActionDatasource;
    this.where = where;
    this.aggregationTableExpression = aggregationTableExpression;
    this.materializedFrom = materializedFrom;
    this.missingValue = missingValue;
    this.relativeDaysFrom = relativeDaysFrom;
    this.relativeDaysTo = relativeDaysTo;
    this.windowAggregation = windowAggregation;
    this.canMaterialize = canMaterialize;
  }

  public UserActionColumn(
      Column column,
      UserActionDatasource userActionDatasource,
      Optional<String> where,
      String aggregationTableExpression,
      Optional<LocalDate> materializedFrom,
      String missingValue,
      int relativeDaysFrom,
      int relativeDaysTo, String windowAggregation
  ) {
    this(column, userActionDatasource, where, aggregationTableExpression, materializedFrom,
        missingValue, relativeDaysFrom, relativeDaysTo, windowAggregation, true);
  }

  public UserActionDatasource getUserActionDatasource() {
    return userActionDatasource;
  }

  public Optional<String> getWhere() {
    return where;
  }

  public String getAggregationTableExpression() {
    return aggregationTableExpression;
  }

  public Optional<LocalDate> getMaterializedFrom() {
    return materializedFrom;
  }

  public boolean canMaterialize() {
    return canMaterialize;
  }

  public String getMissingValue() {
    return missingValue;
  }

  public int getRelativeDaysFrom() {
    return relativeDaysFrom;
  }

  public int getRelativeDaysTo() {
    return relativeDaysTo;
  }

  public String getWindowAggregation() {
    return windowAggregation;
  }
}
