package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrain;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

public class EventLikeDatasource extends TemporalDatasource {
  protected final String timestampColumn;

  public EventLikeDatasource(String id, String label, Optional<String> description,
                             TableReference table, SequencedMap<String, Column> columns,
                             Map<String, Kpi> kpis,
                             TimeGrain minTimeGrain,
                             String dateColumn, String timestampColumn) {
    super(id, label, description, table, columns, kpis,
        minTimeGrain, dateColumn);
    this.timestampColumn = timestampColumn;

    if (!columns.containsKey(timestampColumn)) {
      throw new IllegalArgumentException(
          "Timestamp column not found: " + timestampColumn + " in datasource " + id);
    }
  }

  public Column getTimestampColumn() {
    return column(timestampColumn);
  }
}
