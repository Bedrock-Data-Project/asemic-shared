package com.asemicanalytics.sql.sql.bigquery;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
  public String truncateDate(String column, TimeGrains timeGrain) {
    return switch (timeGrain) {
      case min15 -> throw new UnsupportedOperationException();
      case hour, day, week, month, quarter, year ->
          "DATE_TRUNC(" + column + ", " + timeGrain.name().toUpperCase() + ")";
    };
  }

  @Override
  public String dateAdd(String column, String days) {
    return "DATE_ADD(" + column + ", INTERVAL " + days + " DAY)";
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
    return "DATE_DIFF(%s, DATE '1970-01-01', DAY)".formatted(date);
  }

  @Override
  public String dateDiff(String from, String to) {
    return "DATE_DIFF(" + from + ", " + to + ", DAY)";
  }

  @Override
  public String createTableIfNotExists(TableReference tableReference, List<Column> columns,
                                       Optional<Column> dateColumn) {

    String sql = "CREATE TABLE IF NOT EXISTS " + tableIdentifier(tableReference)
        + " (\n" + columns.stream()
        .map(c -> columnIdentifier(c.getId()) + " " + getBigQueryDataType(c.getDataType()))
        .reduce((a, b) -> a + ",\n" + b)
        .orElse("") + "\n)";
    if (dateColumn.isPresent()) {
      sql += "PARTITION BY DATE_TRUNC(" + columnIdentifier(dateColumn.get().getId()) + ", MONTH);";
    }
    return sql;
  }

  @Override
  public String addColumn(TableReference tableReference, Column column) {
    return "ALTER TABLE " + tableIdentifier(tableReference) + " ADD COLUMN "
        + columnIdentifier(column.getId()) + " " + getBigQueryDataType(column.getDataType());
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
  public String insertOverwrite(TableReference table, String select, String partitionColumn,
                                String partitionValue) {
    return "MERGE " + tableIdentifier(table) + " USING (" + select + ") ON FALSE"
        + " WHEN NOT MATCHED AND " + columnIdentifier(partitionColumn)
        + " = " + constant(partitionValue, DataType.DATE) + " THEN"
        + " INSERT ROW"
        + " WHEN NOT MATCHED BY SOURCE AND "
        + columnIdentifier(partitionColumn) + " = "
        + constant(partitionValue, DataType.DATE) + " THEN DELETE";
  }

  @Override
  public String generateNumberArray(String from, String to) {
    return "GENERATE_ARRAY(" + from + ", " + to + ")";
  }

  private String getBigQueryDataType(DataType dataType) {
    return switch (dataType) {
      case NUMBER -> "FLOAT64";
      case INTEGER -> "INT64";
      case BOOLEAN -> "BOOL";
      case STRING -> "STRING";
      case DATE -> "DATE";
      case DATETIME -> "TIMESTAMP";
    };
  }
}
