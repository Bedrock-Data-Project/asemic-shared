package com.asemicanalytics.sql.h2;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import java.util.Arrays;
import java.util.StringJoiner;

public class H2Dialect implements Dialect {
  @Override
  public String referenceAliasedExpression(String renderedExpression, String alias) {
    return columnIdentifier(alias);
  }

  @Override
  public String constant(String value, DataType dataType) {
    return switch (dataType) {
      case NUMBER, INTEGER, BOOLEAN -> value;
      case STRING -> "'" + value + "'";
      case DATE -> "PARSEDATETIME('" + value + "', 'yyyy-MM-dd')";
      case DATETIME -> "PARSEDATETIME('" + value + "', 'yyyy-MM-dd HH:mm:ss')";
    };
  }

  @Override
  public String columnIdentifier(String identifier) {
    var joiner = new StringJoiner(".");
    Arrays.stream(identifier.split("\\.")).forEach(x -> joiner.add("\"" + x + "\""));
    return joiner.toString();
  }

  @Override
  public String tableIdentifier(TableReference table) {
    return table.schemaName()
        .map(schema -> columnIdentifier(schema) + "." + columnIdentifier(table.tableName()))
        .orElse(columnIdentifier(table.tableName()));
  }

  @Override
  public String ifExpression(String condition, String ifTrue, String ifFalse) {
    return "CASE WHEN " + condition + " THEN " + ifTrue + " ELSE " + ifFalse + " END";
  }

  @Override
  public String truncateTimestamp(String column, TimeGrains timeGrain, int shiftDays) {
    var expression = switch (timeGrain) {
      case min15 -> "DATEADD('MINUTE', FLOOR(DATEDIFF('MINUTE', '1970-01-01 00:00:00', " + column
          + ") / 15) * 15, '1970-01-01 00:00:00')";
      case hour, day, week, month, quarter, year ->
          "TRUNCATE(" + column + ", '" + timeGrain.name().toUpperCase() + "')";
    };

    if (shiftDays != 0) {
      expression += " + " + shiftDays;
    }
    return expression;
  }

  @Override
  public String covertToTimestamp(String column, int shiftDays) {
    if (shiftDays == 0) {
      return "CAST(" + column + " AS TIMESTAMP)";
    }
    return "CAST(" + column + " AS TIMESTAMP) + " + shiftDays;
  }

  @Override
  public String intervalDays(long days) {
    return "INTERVAL '" + days + "' DAY";
  }

  @Override
  public String epochDays(String date) {
    return "DATEDIFF('day', '1970-01-01', " + date + "::timestamp)";
  }

  @Override
  public String epochSeconds(String timestamp) {
    return "DATEDIFF('SECOND', '1970-01-01 00:00:00', " + timestamp + ")";
  }

  @Override
  public String matchesRegex(String expression, String regex) {
    return "REGEXP_MATCHES(" + expression + ", " + constant(regex, DataType.STRING) + ")";
  }
}
