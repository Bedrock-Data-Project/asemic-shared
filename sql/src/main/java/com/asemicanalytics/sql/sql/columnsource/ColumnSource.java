package com.asemicanalytics.sql.sql.columnsource;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.datasource.Datasource;
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

  public Expression loadColumn(String columnName, DatetimeInterval interval) {
    if (datasource.getTableColumns().containsKey(columnName)) {
      return table().column(columnName);
    }

    if (datasource.getComputedColumns().containsKey(columnName)) {
      var column = datasource.getComputedColumns().get(columnName);
      var columns = Formatter.extractKeys(column.getFormula());
      return new TemplatedExpression(column.getFormula(), TemplateDict.noMissing(
          columns.stream().collect(Collectors.toMap(x -> x, x -> loadColumn(x, interval)))));
    }

    throw new IllegalStateException("Column not found: " + columnName);
  }

  public Datasource getDatasource() {
    return datasource;
  }

}
