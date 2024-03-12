package com.asemicanalytics.sql.sql.builder.tablelike;

import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.TableColumn;

public abstract class TableLike implements Token {
  public Expression column(String columnName) {
    return new TableColumn(this, columnName);
  }

  public Expression column(String columnName, String alias) {
    return column(columnName).withAlias(alias);
  }
}
