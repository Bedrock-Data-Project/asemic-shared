package com.asemicanalytics.core.datasource.useraction;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RegistrationUserActionDatasource extends UserActionDatasource {
  public static final String DATASOURCE_TAG = "registration_datasource";
  public static final String COUNTRY_COLUMN_TAG = "country_column";
  public static final String PLATFORM_COLUMN_TAG = "platform_column";
  public static final String BUILD_VERSION_COLUMN_TAG = "build_version_column";

  private final Optional<String> countryColumn;
  private final Optional<String> platformColumn;
  private final Optional<String> buildVersionColumn;

  public RegistrationUserActionDatasource(String id, String label, Optional<String> description,
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
