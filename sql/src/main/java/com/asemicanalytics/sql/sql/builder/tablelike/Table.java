package com.asemicanalytics.sql.sql.builder.tablelike;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;

public class Table implements TableLike {
  private final TableReference table;

  public Table(TableReference table) {
    this.table = table;
  }

  public TableReference getTable() {
    return table;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.tableIdentifier(table);
  }
}
