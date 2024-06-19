package com.asemicanalytics.core.datasource.userwide;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.core.datasource.TemporalDatasource;
import com.asemicanalytics.core.datasource.useraction.ActivityUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.RegistrationUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UserWideDatasource extends TemporalDatasource {
  public static final String REGISTRATION_DATE_COLUMN = "registration_date";
  public static final String COHORT_DAY_COLUMN = "cohort_day";
  public static final String COHORT_SIZE_COLUMN = "cohort_size";
  public static final String DAU_DATE = "_dau_date";
  public static final String LAST_LOGIN_DATE_COLUMN = "last_login_date";

  private final RegistrationUserActionDatasource registrationDatasource;
  private final ActivityUserActionDatasource activityDatasource;
  private final String baseTablePrefix;
  private final TableReference baseTable;

  private final List<Integer> activityTablesDays;
  private final List<Integer> cohortTableDays;

  public UserWideDatasource(String baseTable,
                            Optional<Columns> columns,
                            RegistrationUserActionDatasource registrationDatasource,
                            ActivityUserActionDatasource activityDatasource,
                            List<Integer> activityTablesDays,
                            List<Integer> cohortTableDays,
                            Map<String, Kpi> kpis) {
    super("user_wide", "User Wide", Optional.empty(),
        TableReference.parse(
            userActionTable(baseTable, activityTablesDays.stream().min(Integer::compare).get())),
        withBaseColumns(columns, registrationDatasource, activityDatasource), kpis, TimeGrains.day,
        Set.of());
    this.baseTablePrefix = baseTable;
    this.baseTable = TableReference.parse(baseTable);
    this.registrationDatasource = registrationDatasource;
    this.activityDatasource = activityDatasource;
    this.activityTablesDays = activityTablesDays;
    this.cohortTableDays = cohortTableDays;
  }

  private static Columns withBaseColumns(
      Optional<Columns> columns,
      UserActionDatasource registrationDatasource,
      UserActionDatasource activityDatasource) {

    var baseColumns = List.of(
        new RegistrationColumn(registrationDatasource.getDateColumn(),
            registrationDatasource.getDateColumn().getId()),
        new RegistrationColumn(registrationDatasource.getUserIdColumn(),
            registrationDatasource.getUserIdColumn().getId()),
        new RegistrationColumn(new Column(
            REGISTRATION_DATE_COLUMN,
            DataType.DATE,
            "Registration Date",
            Optional.empty(),
            true,
            true,
            Set.of()
        ), registrationDatasource.getDateColumn().getId()),
        new ComputedColumn(
            new Column(
                COHORT_DAY_COLUMN,
                DataType.INTEGER,
                "Cohort Day",
                Optional.empty(),
                true,
                true,
                Set.of()
            ),
            "{EPOCH_DAYS(" + registrationDatasource.getDateColumn().getId()
                + ")} - {EPOCH_DAYS(" + REGISTRATION_DATE_COLUMN + ")}"
        ),
        new ComputedColumn(
            new Column(
                COHORT_SIZE_COLUMN,
                DataType.INTEGER,
                "Cohort Size",
                Optional.empty(),
                false,
                false,
                Set.of()
            ),
            "1"
        ),
        new UserActionColumn(
            new Column(
                DAU_DATE,
                DataType.DATE,
                "DAU Date",
                Optional.of("Helper column for last_login_date"),
                false,
                false,
                Set.of()
            ),
            activityDatasource,
            Optional.empty(),
            "MAX({" + activityDatasource.getDateColumn().getId() + "})",
            Optional.empty(),
            null,
            0,
            0,
            "MAX",
            false
        ),
        new TotalColumn(
            new Column(
                LAST_LOGIN_DATE_COLUMN,
                DataType.DATE,
                "Last Login Date",
                Optional.empty(),
                true,
                true,
                Set.of()
            ),
            DAU_DATE,
            "COALESCE({__current}, {__total})"
        )
    );

    var mergedColumns = columns
        .map(c -> new LinkedHashMap(c.getColumns()))
        .orElse(new LinkedHashMap<>());
    baseColumns.forEach(column -> mergedColumns.putIfAbsent(column.getId(), column));

    return new Columns(mergedColumns);
  }

  public static String cohortTable(String baseTablePrefix) {
    return baseTablePrefix + "_cohort";
  }

  public TableReference cohortTable() {
    return new TableReference(baseTable.schemaName(), cohortTable(baseTable.tableName()));
  }

  public static String totalsTable(String baseTablePrefix) {
    return baseTablePrefix + "_total";
  }

  public TableReference totalsTable() {
    return new TableReference(baseTable.schemaName(), totalsTable(baseTable.tableName()));
  }

  public static String userActionTable(String baseTablePrefix, int days) {
    return baseTablePrefix + "_" + days + "d";
  }

  public Column getUserIdColumn() {
    return columns.column(this.registrationDatasource.getUserIdColumn().getId());
  }

  public Column getRegistrationDateColumn() {
    return columns.column(REGISTRATION_DATE_COLUMN);
  }

  public TableReference getBaseTable() {
    return baseTable;
  }

  @Override
  public String getType() {
    return "user_wide";
  }

  public UserActionDatasource getRegistrationDatasource() {
    return registrationDatasource;
  }

  public UserActionDatasource getActivityDatasource() {
    return activityDatasource;
  }

  public List<Integer> getActivityTablesDays() {
    return activityTablesDays;
  }

  public List<Integer> getCohortTableDays() {
    return cohortTableDays;
  }

  public String getBaseTablePrefix() {
    return baseTablePrefix;
  }
}

