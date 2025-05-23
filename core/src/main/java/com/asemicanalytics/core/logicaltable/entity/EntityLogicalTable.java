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
  public static final String REGISTRATION_DATE_COLUMN = "registration_date";
  public static final String COHORT_DAY_COLUMN = "cohort_day";
  public static final String DAYS_SINCE_LAST_ACTIVE = "days_since_last_active";
  public static final String COHORT_SIZE_COLUMN = "cohort_size";
  public static final String LAST_ACTIVITY_DATE_COLUMN = "last_activity_date";

  private final RegistrationsLogicalTable registrationLogicalTable;
  private final ActivityLogicalTable activityLogicalTable;
  private final String schema;
  private final TableReference baseTable;

  private final int activityTableDays;
  private final List<Integer> cohortTableDays;

  public EntityLogicalTable(String schema,
                            Optional<Columns<EntityProperty>> columns,
                            RegistrationsLogicalTable registrationLogicalTable,
                            ActivityLogicalTable activityLogicalTable,
                            int activityTableDays,
                            List<Integer> cohortTableDays,
                            Map<String, Kpi> kpis) {
    super("user_wide", "Entity", Optional.empty(),
        TableReference.of(schema, "totals"),
        withBaseColumns(columns, registrationLogicalTable, activityLogicalTable), kpis,
        TimeGrains.day,
        Set.of(),
        withMaterializedIndexTables(schema, activityTableDays,
            cohortTableDays));
    this.schema = schema;
    this.baseTable = TableReference.of(schema, "totals");
    this.registrationLogicalTable = registrationLogicalTable;
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
      String schema,
      int activityTableDays,
      List<Integer> cohortTableDays) {

    return List.of(
        new MaterializedIndexTable(
            TableReference.of(schema, "daily"),
            dailyIndexFilter(),
            1
        ),

        new MaterializedIndexTable(
            TableReference.of(schema, "active"),
            activeIndexFilter(activityTableDays),
            2
        ),

        new MaterializedIndexTable(
            TableReference.of(schema, "cohort"),
            cohortIndexFilter(cohortTableDays),
            3
        )
    );
  }

  public static Columns<EntityProperty> withBaseColumns(
      Optional<Columns<EntityProperty>> columns,
      RegistrationsLogicalTable registrationActionLogicalTable,
      ActivityLogicalTable activityLogicalTable) {

    var baseColumns = List.of(
        new RegistrationColumn(registrationActionLogicalTable.getDateColumn(),
            registrationActionLogicalTable,
            registrationActionLogicalTable.getDateColumn().getId()),
        new RegistrationColumn(registrationActionLogicalTable.entityIdColumn(),
            registrationActionLogicalTable,
            registrationActionLogicalTable.entityIdColumn().getId()),
        new RegistrationColumn(new Column(
            REGISTRATION_DATE_COLUMN,
            DataType.DATE,
            "Registration Date",
            Optional.empty(),
            true,
            true,
            Set.of()
        ), registrationActionLogicalTable,  registrationActionLogicalTable.getDateColumn().getId()),
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
            "{DATE_DIFF(" + REGISTRATION_DATE_COLUMN + ", "
                + registrationActionLogicalTable.getDateColumn().getId()
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
                LAST_ACTIVITY_DATE_COLUMN,
                DataType.DATE,
                "Last Activity Date",
                Optional.empty(),
                true,
                true,
                Set.of()
            ),
            new EventColumn(
                Column.ofHidden(LAST_ACTIVITY_DATE_COLUMN + "__inner", DataType.DATE),
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
                + registrationActionLogicalTable.getDateColumnId() + ", "
                + LAST_ACTIVITY_DATE_COLUMN + ")}")
    );

    var mergedColumns = columns
        .map(c -> new LinkedHashMap<>(c.getColumns()))
        .orElse(new LinkedHashMap<>());
    baseColumns.forEach(column -> mergedColumns.putIfAbsent(column.getId(), column));

    return new Columns<>(mergedColumns);
  }

  public EntityProperty entityIdColumn() {
    return columns.column(this.registrationLogicalTable.entityIdColumn().getId());
  }

  public EntityProperty getRegistrationDateColumn() {
    return columns.column(REGISTRATION_DATE_COLUMN);
  }

  @Override
  public String getType() {
    return "user_wide";
  }

  public RegistrationsLogicalTable getRegistrationLogicalTable() {
    return registrationLogicalTable;
  }

  public ActivityLogicalTable getActivityLogicalTable() {
    return activityLogicalTable;
  }

  public EntityLogicalTable withColumns(Columns columns) {
    return new EntityLogicalTable(
        schema,
        Optional.of(this.columns.add(columns)),
        registrationLogicalTable,
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

