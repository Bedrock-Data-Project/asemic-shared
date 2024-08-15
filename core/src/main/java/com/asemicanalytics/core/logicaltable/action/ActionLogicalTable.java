package com.asemicanalytics.core.logicaltable.action;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.EventLikeLogicalTable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ActionLogicalTable extends EventLikeLogicalTable {
  public static final String ENTITY_ID_COLUMN_TAG = "entity_id_column";

  private final String entityIdColumn;
  private final Optional<String> where;

  public ActionLogicalTable(String id, String label, Optional<String> description,
                            TableReference table,
                            Columns columns,
                            Map<String, Kpi> kpis, Optional<String> where, Set<String> tags) {
    super(id, label, description, table, columns, kpis, tags);
    this.entityIdColumn = columns.getColumnIdByTag(ENTITY_ID_COLUMN_TAG);
    this.where = where;
  }

  public Column entityIdColumn() {
    return columns.column(entityIdColumn);
  }

  public String getEntityIdColumnId() {
    return entityIdColumn;
  }

  @Override
  public String getType() {
    return "action";
  }

  public Optional<String> getWhere() {
    return where;
  }
}
