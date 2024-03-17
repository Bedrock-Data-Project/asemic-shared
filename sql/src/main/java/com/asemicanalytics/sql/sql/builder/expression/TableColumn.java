package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class TableColumn implements Expression {
  private final TableLike table;
  private final String name;

  public TableColumn(TableLike table, String name) {
    super();
    this.table = table;
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public String render(Dialect dialect) {
    return table.render(dialect) + "." + dialect.columnIdentifier(name);
  }
}
