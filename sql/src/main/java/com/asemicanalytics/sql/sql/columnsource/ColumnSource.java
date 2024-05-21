package com.asemicanalytics.sql.sql.columnsource;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.sql.sql.builder.expression.EpochDays;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.Formatter;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.stream.Collectors;

public abstract class ColumnSource {
  private final Datasource datasource;

  protected ColumnSource(Datasource datasource) {
    this.datasource = datasource;
  }

  public abstract TableLike table();

  private Expression parseColumnExpression(String columnExpression, DatetimeInterval interval) {
    // TODO this is very primitive implementation to support cohort_day for user wide
    // we can consider improving the syntax in the future to offer database agnostic definitions
    if (columnExpression.contains("(") && columnExpression.contains(")")) {
      var functionName = columnExpression.substring(0, columnExpression.indexOf("("));
      var arguments = columnExpression
          .substring(columnExpression.indexOf("(") + 1, columnExpression.indexOf(")"))
          .split(",");

      if (functionName.equals("EPOCH_DAYS")) {
        if (arguments.length != 1) {
          throw new IllegalArgumentException("EPOCH_DAYS function requires 1 argument");
        }
        return new EpochDays(loadColumn(arguments[0], interval));
      }

      throw new IllegalArgumentException("Unknown function: " + functionName);
    } else {
      return loadColumn(columnExpression, interval);
    }
  }

  public Expression loadColumn(String columnName, DatetimeInterval interval) {
    var column = datasource.column(columnName);
    if (column == null) {
      throw new IllegalArgumentException(
          "Column not found: " + columnName + " in datasource " + datasource.getId());
    }

    if (column instanceof ComputedColumn computedColumn) {
      var columns = Formatter.extractKeys(computedColumn.getFormula());
      return new TemplatedExpression(
          computedColumn.getFormula(),
          TemplateDict.noMissing(
              columns
                  .stream()
                  .collect(Collectors.toMap(x -> x, x -> parseColumnExpression(x, interval)))));
    }

    return table().column(columnName);
  }

  public Datasource getDatasource() {
    return datasource;
  }

}
