package com.asemicanalytics.sql.sql.columnsource;

import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class TableColumnSource extends ColumnSource {
  private final TableLike tableLike;

  public TableColumnSource(Datasource datasource, TableLike tableLike) {
    super(datasource);
    this.tableLike = tableLike;
  }

  @Override
  public TableLike table() {
    return tableLike;
  }

}
