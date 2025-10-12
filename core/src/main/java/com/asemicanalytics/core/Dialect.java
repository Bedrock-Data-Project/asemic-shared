package com.asemicanalytics.core;


import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.Optional;

public interface Dialect {
  String referenceAliasedExpression(String renderedExpression, String alias);

  String constant(String value, DataType dataType);

  String columnIdentifier(String identifier);

  String tableIdentifier(TableReference tableReference);

  String ifExpression(String condition, String ifTrue, String ifFalse);

  String truncateDate(String column, TimeGrains timeGrain);

  String dateAdd(String column, String days);

  String covertToTimestamp(String column, int shiftDays);

  String intervalDays(long days);

  String epochDays(String date);

  String dateDiff(String from, String to);

  String createTableIfNotExists(
      TableReference tableReference, List<Column> columns, Optional<Column> dateColumn);

  String addColumns(TableReference tableReference, List<Column> columns);

  String arrayOffset(String arrayExpression, int offset);

  default String caseExpression(String switchExpression, String whenThens, String ifFalse) {
    return "CASE " + switchExpression + " " + whenThens + " " + ifFalse + " END";
  }

  default String caseExpression(String whenThens, String ifFalse) {
    return "CASE " + whenThens + " " + ifFalse + " END";
  }

  default String caseWhenThen(String when, String then) {
    return "WHEN " + when + " THEN " + then;
  }

  default String caseElse(String elseExpression) {
    return "ELSE " + elseExpression;
  }

  default String createTableFromSelect(String select, TableReference tableReference,
                                       boolean replace) {
    if (replace) {
      return "CREATE OR REPLACE TABLE " + tableIdentifier(tableReference) + " AS\n" + select;
    } else {
      return "CREATE TABLE " + tableIdentifier(tableReference) + " AS\n" + select;
    }
  }

  String epochSeconds(String timestamp);

  String matchesRegex(String expression, String regex);

  /**
   * If a database does not support INSERT OVERWRITE, this method can be overriden
   * to delete the partition before inserting the data with insertOverwrite.
   */
  default Optional<String> prepareForInsertOverwrite(
      TableReference table, String partitionColumn, DateInterval partitionValue) {
    return Optional.empty();
  }

  String insertOverwrite(
      TableReference table, String insert, String partitionColumn, DateInterval partitionValue);

  String generateNumberArray(String from, String to);

  default String unnestIdentifier(String identifier) {
    return columnIdentifier(identifier);
  }

  String getDataType(DataType dataType);

  default String cast(String expression, DataType dataType) {
    return "CAST(" + expression + " AS " + getDataType(dataType) + ")";
  }
}

