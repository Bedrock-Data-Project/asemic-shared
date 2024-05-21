package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrain;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

public class UserActionDatasource extends EventLikeDatasource {
  private final String userIdColumn;

  public UserActionDatasource(String id, String label, Optional<String> description,
                              TableReference table,
                              SequencedMap<String, Column> columns,
                              Map<String, Kpi> kpis,
                              TimeGrain minTimeGrain,
                              String dateColumn, String timestampColumn, String userIdColumn) {
    super(id, label, description, table, columns, kpis, minTimeGrain,
        dateColumn, timestampColumn);
    this.userIdColumn = userIdColumn;
    if (!columns.containsKey(userIdColumn)) {
      throw new IllegalArgumentException(
          "User Id column not found: " + userIdColumn + " in datasource " + id);
    }
  }

  public Column getUserIdColumn() {
    return column(userIdColumn);
  }

  @Override
  public String getType() {
    return "user_action";
  }
}
