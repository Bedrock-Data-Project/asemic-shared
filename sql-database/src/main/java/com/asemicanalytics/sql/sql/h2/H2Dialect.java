package com.asemicanalytics.sql.sql.h2;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
  public String dateAdd(String column, String days) {
    return "DATEADD(DAY, %s, %s)".formatted(days, column);
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
  public String dateDiff(String from, String to) {
    return "DATEDIFF('day', " + from + "::timestamp, " + to + "::timestamp)";
  }

  @Override
  public String createTableIfNotExists(TableReference tableReference, List<Column> columns,
                                       Optional<Column> dateColumn) {
    String sql = "CREATE TABLE IF NOT EXISTS " + tableIdentifier(tableReference)
        + " (\n" + columns.stream()
        .map(c -> columnIdentifier(c.getId()) + " " + getH2DataType(c.getDataType()))
        .reduce((a, b) -> a + ",\n" + b)
        .orElse("") + "\n)";
    return sql;
  }

  @Override
  public String addColumn(TableReference tableReference, Column column) {
    return "ALTER TABLE " + tableIdentifier(tableReference) + " ADD  "
        + columnIdentifier(column.getId()) + " " + getH2DataType(column.getDataType());
  }

  @Override
  public String epochSeconds(String timestamp) {
    return "DATEDIFF('SECOND', '1970-01-01 00:00:00', " + timestamp + ")";
  }

  @Override
  public String matchesRegex(String expression, String regex) {
    return "REGEXP_MATCHES(" + expression + ", " + constant(regex, DataType.STRING) + ")";
  }

  @Override
  public Optional<String> prepareForInsertOverwrite(TableReference table, String partitionColumn,
                                                    String partitionValue) {
    return Optional.of("DELETE FROM " + tableIdentifier(table)
        + " WHERE " + columnIdentifier(partitionColumn)
        + " = " + constant(partitionValue, DataType.DATE));
  }

  @Override
  public String insertOverwrite(TableReference table, String select, String partitionColumn,
                                String partitionValue) {
    return "INSERT INTO  " + tableIdentifier(table)
        + "\n" + select;
  }

  @Override
  public String generateNumberArray(String from, String to) {
    return "ARRAY(SELECT * FROM SYSTEM_RANGE(%s, %s))".formatted(from, to);
  }

  private String getH2DataType(DataType dataType) {
    return switch (dataType) {
      case NUMBER -> "DOUBLE";
      case INTEGER -> "INT";
      case BOOLEAN -> "BOOLEAN";
      case STRING -> "VARCHAR(50)";
      case DATE -> "DATE";
      case DATETIME -> "TIMESTAMP";
    };
  }

  @Override
  public String unnestIdentifier(String identifier) {
    return "t(%s)".formatted(columnIdentifier(identifier));
  }
}
