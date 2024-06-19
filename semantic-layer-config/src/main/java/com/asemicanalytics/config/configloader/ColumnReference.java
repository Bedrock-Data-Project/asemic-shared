package com.asemicanalytics.config.configloader;


import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.datasource.Datasource;

public record ColumnReference(
    Datasource datasource,
    String columnId
) {
  public Column column() {
    return datasource.getColumns().column(columnId);
  }
}
