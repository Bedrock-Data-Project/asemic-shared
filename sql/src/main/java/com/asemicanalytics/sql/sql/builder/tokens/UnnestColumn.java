package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

class UnnestColumn extends TableColumn {
  public UnnestColumn(TableLike table, String name) {
    super(table, name);
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.columnIdentifier(name());
  }
}
