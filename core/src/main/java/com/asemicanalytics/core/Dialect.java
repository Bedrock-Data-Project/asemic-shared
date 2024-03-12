package com.asemicanalytics.core;


public interface Dialect {
  String referenceAliasedExpression(String renderedExpression, String alias);

  String constant(String value, DataType dataType);

  String columnIdentifier(String identifier);

  String tableIdentifier(TableReference tableReference);

  String ifExpression(String condition, String ifTrue, String ifFalse);

  String truncateTimestamp(String column, TimeGrains timeGrain, int shiftDays);

  String covertToTimestamp(String column, int shiftDays);

  String intervalDays(long days);

  String epochDays(String date);
}
