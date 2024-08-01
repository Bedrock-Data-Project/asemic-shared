package com.asemicanalytics.core.logicaltable.action;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FirstAppearanceActionLogicalTable extends ActionLogicalTable {
  public static final String TAG = "first_appearance_action";
  public static final String FIRST_APPEARANCE_PROPERTY_TAG = "first_appearance_property";

  public FirstAppearanceActionLogicalTable(String id, String label, Optional<String> description,
                                           TableReference table,
                                           Columns columns,
                                           Map<String, Kpi> kpis, Set<String> tags) {
    super(id, label, description, table, columns, kpis, tags);
  }

}
