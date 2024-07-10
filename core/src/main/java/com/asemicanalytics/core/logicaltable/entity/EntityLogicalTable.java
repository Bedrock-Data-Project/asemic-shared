package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EntityLogicalTable extends TemporalLogicalTable {
  public static final String FIRST_APPEARANCE_DATE_COLUMN = "registration_date";
  public static final String COHORT_DAY_COLUMN = "cohort_day";
  public static final String COHORT_SIZE_COLUMN = "cohort_size";
  public static final String DAU_DATE = "_dau_date";
  public static final String LAST_LOGIN_DATE_COLUMN = "last_login_date";

  private final FirstAppearanceActionLogicalTable firstAppearanceActionLogicalTable;
  private final ActivityLogicalTable activityLogicalTable;
  private final String baseTablePrefix;
  private final TableReference baseTable;

  private final List<Integer> activityTablesDays;
  private final List<Integer> cohortTableDays;

  public EntityLogicalTable(String baseTable,
                            Optional<Columns> columns,
                            FirstAppearanceActionLogicalTable firstAppearanceActionLogicalTable,
                            ActivityLogicalTable activityLogicalTable,
                            List<Integer> activityTablesDays,
                            List<Integer> cohortTableDays,
                            Map<String, Kpi> kpis) {
    super("user_wide", "Entity", Optional.empty(),
        TableReference.parse(
            actionTable(baseTable, activityTablesDays.stream().min(Integer::compare).get())),
        withBaseColumns(columns, firstAppearanceActionLogicalTable, activityLogicalTable), kpis,
        TimeGrains.day,
        Set.of());
    this.baseTablePrefix = baseTable;
    this.baseTable = TableReference.parse(baseTable);
    this.firstAppearanceActionLogicalTable = firstAppearanceActionLogicalTable;
    this.activityLogicalTable = activityLogicalTable;
    this.activityTablesDays = activityTablesDays;
    this.cohortTableDays = cohortTableDays;
  }

  private static Columns withBaseColumns(
      Optional<Columns> columns,
      FirstAppearanceActionLogicalTable firstAppearanceActionLogicalTable,
      ActivityLogicalTable activityLogicalTable) {

    var baseColumns = List.of(
        new FirstAppearanceColumn(firstAppearanceActionLogicalTable.getDateColumn(),
            firstAppearanceActionLogicalTable.getDateColumn().getId()),
        new FirstAppearanceColumn(firstAppearanceActionLogicalTable.entityIdColumn(),
            firstAppearanceActionLogicalTable.entityIdColumn().getId()),
        new FirstAppearanceColumn(new Column(
            FIRST_APPEARANCE_DATE_COLUMN,
            DataType.DATE,
            "Registration Date",
            Optional.empty(),
            true,
            true,
            Set.of()
        ), firstAppearanceActionLogicalTable.getDateColumn().getId()),
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
            "{EPOCH_DAYS(" + firstAppearanceActionLogicalTable.getDateColumn().getId()
                + ")} - {EPOCH_DAYS(" + FIRST_APPEARANCE_DATE_COLUMN + ")}"
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
        new ActionColumn(
            new Column(
                DAU_DATE,
                DataType.DATE,
                "DAU Date",
                Optional.of("Helper column for last_login_date"),
                false,
                false,
                Set.of()
            ),
            activityLogicalTable,
            Optional.empty(),
            "MAX({" + activityLogicalTable.getDateColumn().getId() + "})",
            Optional.empty(),
            null,
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
            "last_value"
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

  public static String actionTable(String baseTablePrefix, int days) {
    return baseTablePrefix + "_" + days + "d";
  }

  public Column entityIdColumn() {
    return columns.column(this.firstAppearanceActionLogicalTable.entityIdColumn().getId());
  }

  public Column getFirstAppearanceDateColumn() {
    return columns.column(FIRST_APPEARANCE_DATE_COLUMN);
  }

  public TableReference getBaseTable() {
    return baseTable;
  }

  @Override
  public String getType() {
    return "user_wide";
  }

  public ActionLogicalTable getFirstAppearanceActionLogicalTable() {
    return firstAppearanceActionLogicalTable;
  }

  public ActionLogicalTable getActivityLogicalTable() {
    return activityLogicalTable;
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

