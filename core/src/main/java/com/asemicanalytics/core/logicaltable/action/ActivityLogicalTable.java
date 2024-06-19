package com.asemicanalytics.core.logicaltable.action;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ActivityLogicalTable extends ActionLogicalTable {
  public static final String TAG = "activity_action";

  public static final String COUNTRY_COLUMN_TAG =
      FirstAppearanceActionLogicalTable.COUNTRY_COLUMN_TAG;
  public static final String PLATFORM_COLUMN_TAG =
      FirstAppearanceActionLogicalTable.PLATFORM_COLUMN_TAG;
  public static final String BUILD_VERSION_COLUMN_TAG =
      FirstAppearanceActionLogicalTable.BUILD_VERSION_COLUMN_TAG;

  private final Optional<String> countryColumn;
  private final Optional<String> platformColumn;
  private final Optional<String> buildVersionColumn;

  public ActivityLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns columns,
                              Map<String, Kpi> kpis, Set<String> tags) {
    super(id, label, description, table, columns, kpis, tags);
    this.countryColumn = columns.getColumnIdByTagIfExists(COUNTRY_COLUMN_TAG);
    this.platformColumn = columns.getColumnIdByTagIfExists(PLATFORM_COLUMN_TAG);
    this.buildVersionColumn = columns.getColumnIdByTagIfExists(BUILD_VERSION_COLUMN_TAG);
  }

  public Optional<Column> getCountryColumn() {
    return countryColumn.map(columns::column);
  }

  public Optional<Column> getPlatformColumn() {
    return platformColumn.map(columns::column);
  }

  public Optional<Column> getBuildVersionColumn() {
    return buildVersionColumn.map(columns::column);
  }
}
