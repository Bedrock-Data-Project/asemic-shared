package com.asemicanalytics.sql.sql.snowflake;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DateInterval;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class SnowflakeDialect implements Dialect {
  @Override
  public String referenceAliasedExpression(String renderedExpression, String alias) {
    return columnIdentifier(alias);
  }

  @Override
  public String constant(String value, DataType dataType) {
    return switch (dataType) {
      case NUMBER, INTEGER, BOOLEAN -> value;
      case STRING -> "'" + value + "'";
      case DATE -> "TO_DATE('" + value + "', 'YYYY-MM-DD')";
      case DATETIME -> "TO_TIMESTAMP('" + value + "', 'YYYY-MM-DD HH24:MI:SS.FF3')";
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
    return "\"" + table.schemaName().map(schema -> schema + "\".\"" + table.tableName())
        .orElse(table.tableName()) + "\"";
  }


  @Override
  public String ifExpression(String condition, String ifTrue, String ifFalse) {
    return "IFF(" + condition + ", " + ifTrue + ", " + ifFalse + ")";
  }

  @Override
  public String truncateDate(String column, TimeGrains timeGrain) {
    return switch (timeGrain) {
      case min15 -> throw new UnsupportedOperationException();
      case hour, day, week, month, quarter, year ->
          "DATE_TRUNC(" + timeGrain.name().toUpperCase() + ", " + column + ")";
    };
  }

  @Override
  public String dateAdd(String column, String days) {
    return "DATEADD(DAY, " + days + ", " + column + ")";
  }

  @Override
  public String covertToTimestamp(String column, int shiftDays) {
    if (shiftDays == 0) {
      return "TO_TIMESTAMP(" + column + ")";
    }
    return "TO_TIMESTAMP(" + column + ") + INTERVAL " + shiftDays + " DAY";
  }

  @Override
  public String intervalDays(long days) {
    return "INTERVAL '" + days + "' DAY";
  }

  @Override
  public String epochDays(String date) {
    // TODO untested
    return "DATE_PART('day', " + date + "::timestamp)";
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
  public String addColumns(TableReference tableReference, List<Column> columns) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String epochSeconds(String timestamp) {
    return "DATE_PART('second', " + timestamp + ")";
  }

  @Override
  public String matchesRegex(String expression, String regex) {
    return "REGEXP_LIKE(" + expression + ", " + constant(regex, DataType.STRING) + ")";

  }

  @Override
  public String insertOverwrite(TableReference table, String insert, String partitionColumn,
                                DateInterval partitionValue) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String generateNumberArray(String from, String to) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String getDataType(DataType dataType) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
