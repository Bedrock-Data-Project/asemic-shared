package com.asemicanalytics.sql.sql.h2;

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
import java.util.stream.Collectors;

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
  public String truncateDate(String column, TimeGrains timeGrain) {
    return switch (timeGrain) {
      case min5, min15, min105, min450, min900, hour3 -> throw new UnsupportedOperationException();
      case hour, day, week, month, quarter, year ->
          "DATE_TRUNC('" + timeGrain.name().toUpperCase() + "', " + column + ")";
    };
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
  public String addColumns(TableReference tableReference, List<Column> columns) {
    return columns.stream()
        .map(c -> "ALTER TABLE " + tableIdentifier(tableReference) + " "
            + "ADD "
            + columnIdentifier(c.getId()) + " "
            + getH2DataType(c.getDataType()) + ";")
        .collect(Collectors.joining("\n"));
  }

  @Override
  public String arrayOffset(String arrayExpression, int offset) {
    return arrayExpression + "[" + (offset + 1) + "]";
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
                                                    DateInterval partitionValue) {
    return Optional.of("DELETE FROM " + tableIdentifier(table)
        + " WHERE " + columnIdentifier(partitionColumn)
        + " BETWEEN " + constant(partitionValue.from().toString(), DataType.DATE) + " AND "
        + constant(partitionValue.to().toString(), DataType.DATE));
  }

  @Override
  public String insertOverwrite(TableReference table, String insert, String partitionColumn,
                                DateInterval partitionValue) {
    return insert;
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

  @Override
  public String getDataType(DataType dataType) {
    return getH2DataType(dataType);
  }
}
