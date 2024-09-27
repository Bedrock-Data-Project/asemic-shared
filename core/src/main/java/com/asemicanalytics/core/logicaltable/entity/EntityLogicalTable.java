package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.logicaltable.MaterializedIndexTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.event.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.event.RegistrationsLogicalTable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityLogicalTable extends TemporalLogicalTable<EntityProperty> {
  public static final String FIRST_APPEARANCE_DATE_COLUMN = "first_appearance_date";
  public static final String COHORT_DAY_COLUMN = "cohort_day";
  public static final String DAYS_SINCE_LAST_ACTIVE = "days_since_last_active";
  public static final String COHORT_SIZE_COLUMN = "cohort_size";
  public static final String LAST_LOGIN_DATE_COLUMN = "last_login_date";

  private final RegistrationsLogicalTable firstAppearanceActionLogicalTable;
  private final ActivityLogicalTable activityLogicalTable;
  private final String baseTablePrefix;
  private final TableReference baseTable;

  private final int activityTableDays;
  private final List<Integer> cohortTableDays;

  public EntityLogicalTable(String baseTable,
                            Optional<Columns<EntityProperty>> columns,
                            RegistrationsLogicalTable firstAppearanceActionLogicalTable,
                            ActivityLogicalTable activityLogicalTable,
                            int activityTableDays,
                            List<Integer> cohortTableDays,
                            Map<String, Kpi> kpis) {
    super("user_wide", "Entity", Optional.empty(),
        TableReference.parse(baseTable).withTableSuffix("_totals"),
        withBaseColumns(columns, firstAppearanceActionLogicalTable, activityLogicalTable), kpis,
        TimeGrains.day,
        Set.of(),
        withMaterializedIndexTables(TableReference.parse(baseTable), activityTableDays,
            cohortTableDays));
    this.baseTablePrefix = baseTable;
    this.baseTable = TableReference.parse(baseTable);
    this.firstAppearanceActionLogicalTable = firstAppearanceActionLogicalTable;
    this.activityLogicalTable = activityLogicalTable;
    this.activityTableDays = activityTableDays;
    this.cohortTableDays = cohortTableDays;
  }

  public static String dailyIndexFilter() {
    return "{days_since_last_active} = 0";
  }

  public static String activeIndexFilter(int activityTableDays) {
    return "{days_since_last_active} <= " + activityTableDays;
  }

  public static String cohortIndexFilter(List<Integer> cohortTableDays) {
    return "{cohort_day} IN ("
        + cohortTableDays.stream()
        .map(Object::toString)
        .collect(Collectors.joining(", ")) + ")";
  }

  private static List<MaterializedIndexTable> withMaterializedIndexTables(
      TableReference baseTable,
      int activityTableDays,
      List<Integer> cohortTableDays) {

    return List.of(
        new MaterializedIndexTable(
            baseTable.withTableSuffix("_daily"),
            dailyIndexFilter(),
            1
        ),

        new MaterializedIndexTable(
            baseTable.withTableSuffix("_active"),
            activeIndexFilter(activityTableDays),
            2
        ),

        new MaterializedIndexTable(
            baseTable.withTableSuffix("_cohort"),
            cohortIndexFilter(cohortTableDays),
            3
        )
    );
  }

  public static Columns<EntityProperty> withBaseColumns(
      Optional<Columns<EntityProperty>> columns,
      RegistrationsLogicalTable firstAppearanceActionLogicalTable,
      ActivityLogicalTable activityLogicalTable) {

    var baseColumns = List.of(
        new RegistrationColumn(firstAppearanceActionLogicalTable.getDateColumn(),
            firstAppearanceActionLogicalTable.getDateColumn().getId()),
        new RegistrationColumn(firstAppearanceActionLogicalTable.entityIdColumn(),
            firstAppearanceActionLogicalTable.entityIdColumn().getId()),
        new RegistrationColumn(new Column(
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
            "{DATE_DIFF(" + FIRST_APPEARANCE_DATE_COLUMN + ", "
                + firstAppearanceActionLogicalTable.getDateColumn().getId()
                + ")}"
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
        new LifetimeColumn(
            new Column(
                LAST_LOGIN_DATE_COLUMN,
                DataType.DATE,
                "Last Login Date",
                Optional.empty(),
                true,
                true,
                Set.of()
            ),
            new EventColumn(
                Column.ofHidden(LAST_LOGIN_DATE_COLUMN + "__inner", DataType.DATE),
                activityLogicalTable,
                Optional.empty(),
                "{" + activityLogicalTable.getDateColumnId() + "}",
                EventColumn.AggregateFunction.MAX,
                null),
            LifetimeColumn.MergeFunction.LAST_VALUE
        ),
        new ComputedColumn(
            new Column(
                DAYS_SINCE_LAST_ACTIVE,
                DataType.INTEGER,
                "Days since last active",
                Optional.empty(),
                true,
                false,
                Set.of()
            ),
            "{DATE_DIFF("
                + firstAppearanceActionLogicalTable.getDateColumnId() + ", "
                + LAST_LOGIN_DATE_COLUMN + ")}")
    );

    var mergedColumns = columns
        .map(c -> new LinkedHashMap<>(c.getColumns()))
        .orElse(new LinkedHashMap<>());
    baseColumns.forEach(column -> mergedColumns.putIfAbsent(column.getId(), column));

    return new Columns<>(mergedColumns);
  }

  public EntityProperty entityIdColumn() {
    return columns.column(this.firstAppearanceActionLogicalTable.entityIdColumn().getId());
  }

  public EntityProperty getFirstAppearanceDateColumn() {
    return columns.column(FIRST_APPEARANCE_DATE_COLUMN);
  }

  @Override
  public String getType() {
    return "user_wide";
  }

  public RegistrationsLogicalTable getFirstAppearanceActionLogicalTable() {
    return firstAppearanceActionLogicalTable;
  }

  public ActivityLogicalTable getActivityLogicalTable() {
    return activityLogicalTable;
  }

  public String getBaseTablePrefix() {
    return baseTablePrefix;
  }

  public EntityLogicalTable withColumns(Columns columns) {
    return new EntityLogicalTable(
        baseTablePrefix,
        Optional.of(this.columns.add(columns)),
        firstAppearanceActionLogicalTable,
        activityLogicalTable,
        activityTableDays,
        cohortTableDays,
        kpis
    );
  }

  public int getActivityTableDays() {
    return activityTableDays;
  }

  public List<Integer> getCohortTableDays() {
    return cohortTableDays;
  }
}

