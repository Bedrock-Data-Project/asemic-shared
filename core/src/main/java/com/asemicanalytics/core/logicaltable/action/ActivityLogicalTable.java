package com.asemicanalytics.core.logicaltable.action;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ActivityLogicalTable extends EventLogicalTable {
  public static final String TAG = "activity_action";
  public static final String LAST_LOGIN_PROPERTY_TAG = "last_login_property";


  public ActivityLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns columns,
                              Map<String, Kpi> kpis, Optional<String> where, Set<String> tags) {
    super(id, label, description, table, columns, kpis, where, tags);
  }
}
