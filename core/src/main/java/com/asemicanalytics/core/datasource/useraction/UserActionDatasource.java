package com.asemicanalytics.core.datasource.useraction;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.EventLikeDatasource;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UserActionDatasource extends EventLikeDatasource {
  public static final String USER_ID_COLUMN_TAG = "user_id_column";

  private final String userIdColumn;

  public UserActionDatasource(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns columns,
                              Map<String, Kpi> kpis, Set<String> tags) {
    super(id, label, description, table, columns, kpis, tags);
    this.userIdColumn = columns.getColumnIdByTag(USER_ID_COLUMN_TAG);
  }

  public Column getUserIdColumn() {
    return columns.column(userIdColumn);
  }

  public String getUserIdColumnId() {
    return userIdColumn;
  }

  @Override
  public String getType() {
    return "user_action";
  }
}
