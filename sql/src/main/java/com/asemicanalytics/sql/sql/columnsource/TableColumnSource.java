package com.asemicanalytics.sql.sql.columnsource;

import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class TableColumnSource extends ColumnSource {
  private final TableLike tableLike;

  public TableColumnSource(LogicalTable logicalTable, TableLike tableLike) {
    super(logicalTable);
    this.tableLike = tableLike;
  }

  @Override
  public TableLike table() {
    return tableLike;
  }

}
