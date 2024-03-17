package com.asemicanalytics.sql.bigquery;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import java.util.Arrays;
import java.util.StringJoiner;

public class BigQueryDialect implements Dialect {
  @Override
  public String referenceAliasedExpression(String renderedExpression, String alias) {
    return columnIdentifier(alias);
  }

  @Override
  public String constant(String value, DataType dataType) {
    return switch (dataType) {
      case NUMBER, INTEGER, BOOLEAN -> value;
      case STRING -> "'" + value + "'";
      case DATE -> "DATE '" + value + "'";
      case DATETIME -> "TIMESTAMP '" + value + "'";
    };
  }

  @Override
  public String columnIdentifier(String identifier) {
    var joiner = new StringJoiner(".");
    Arrays.stream(identifier.split("\\.")).forEach(x -> joiner.add("`" + x + "`"));
    return joiner.toString();
  }

  @Override
  public String tableIdentifier(TableReference table) {
    return "`" + table.schemaName().map(schema -> schema + "." + table.tableName())
        .orElse(table.tableName()) + "`";
  }


  @Override
  public String ifExpression(String condition, String ifTrue, String ifFalse) {
    return "IF(" + condition + ", " + ifTrue + ", " + ifFalse + ")";
  }

  @Override
  public String truncateTimestamp(String column, TimeGrains timeGrain, int shiftDays) {
    var expression = switch (timeGrain) {
      case min15 -> "TIMESTAMP_ADD(TIMESTAMP_TRUNC(" + column
          + ", HOUR), INTERVAL CAST(EXTRACT(MINUTE FROM " + column
          + ") / 15 AS INT64)*15 MINUTE)";
      case hour, day, week, month, quarter, year ->
          "DATE_TRUNC(" + column + ", " + timeGrain.name().toUpperCase() + ")";
    };
    if (shiftDays != 0) {
      expression = "TIMESTAMP_ADD(" + expression + ", INTERVAL " + shiftDays + " DAY)";
    }
    return expression;
  }

  @Override
  public String covertToTimestamp(String column, int shiftDays) {
    if (shiftDays == 0) {
      return "TIMESTAMP(" + column + ")";
    }
    return "TIMESTAMP(" + column + ") + INTERVAL " + shiftDays + " DAY";
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
  public String epochSeconds(String timestamp) {
    return "UNIX_SECONDS(" + timestamp + ")";
  }
}
