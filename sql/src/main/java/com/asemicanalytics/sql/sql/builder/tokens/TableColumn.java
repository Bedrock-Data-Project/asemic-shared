package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class TableColumn implements Expression {
  private final String name;
  private TableLike table;

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

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    if (table.equals(oldTable)) {
      table = newTable;
    }
  }
}
