package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import java.util.List;
import java.util.Optional;

public class ContentHashDialect implements Dialect {
  @Override
  public String referenceAliasedExpression(String renderedExpression, String alias) {
    return columnIdentifier(alias);
  }

  @Override
  public String constant(String value, DataType dataType) {
    return value;
  }

  @Override
  public String columnIdentifier(String identifier) {
    return identifier;
  }

  @Override
  public String tableIdentifier(TableReference table) {
    return table.schemaName().map(schema -> schema + "." + table.tableName())
        .orElse(table.tableName());
  }


  @Override
  public String ifExpression(String condition, String ifTrue, String ifFalse) {
    return "IF(" + condition + ", " + ifTrue + ", " + ifFalse + ")";
  }

  @Override
  public String truncateDate(String column, TimeGrains timeGrain) {
    return "truncateTimestamp_" + column + "_" + timeGrain;
  }

  @Override
  public String dateAdd(String column, String days) {
    return "dateAdd_" + column + "_" + days;
  }

  @Override
  public String covertToTimestamp(String column, int shiftDays) {
    return "covertToTimestamp_" + column + "_" + shiftDays;
  }

  @Override
  public String intervalDays(long days) {
    return "INTERVAL " + days + " DAY";
  }

  @Override
  public String epochDays(String date) {
    return "UNIX_DATE(" + date + ")";
  }

  @Override
  public String dateDiff(String from, String to) {
    return "DATEDIFF(" + from + ", " + to + ")";
  }

  @Override
  public String createTableIfNotExists(TableReference tableReference, List<Column> columns,
                                       Optional<Column> dateColumn) {

    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String addColumn(TableReference tableReference, Column column) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String epochSeconds(String timestamp) {
    return "UNIX_SECONDS(" + timestamp + ")";
  }

  @Override
  public String matchesRegex(String expression, String regex) {
    return "REGEXP_CONTAINS(" + expression + ", r" + constant(regex, DataType.STRING) + ")";
  }

  @Override
  public String insertOverwrite(TableReference table, String insert, String partitionColumn,
                                DateInterval partitionValue) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String generateNumberArray(String from, String to) {
    return "generateNumberArray_" + from + "_" + to;
  }

  @Override
  public String getDataType(DataType dataType) {
    return dataType.toString();
  }
}
