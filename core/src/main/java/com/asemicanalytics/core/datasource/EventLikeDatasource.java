package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventLikeDatasource extends TemporalDatasource {
  public static final String TIMESTAMP_COLUMN_TAG = "timestamp_column";
  protected final String timestampColumn;

  public EventLikeDatasource(String id, String label, Optional<String> description,
                             TableReference table, Columns columns,
                             Map<String, Kpi> kpis, Set<String> tags) {
    super(id, label, description, table, columns, kpis, TimeGrains.min15, tags);
    this.timestampColumn = columns.getColumnIdByTag(TIMESTAMP_COLUMN_TAG);
  }

  public Column getTimestampColumn() {
    return columns.column(timestampColumn);
  }

  public String getTimestampColumnId() {
    return timestampColumn;
  }
}
