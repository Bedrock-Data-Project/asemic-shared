package com.asemicanalytics.config.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.logicaltable.EventLikeLogicalTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFirstAppearanceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyTotalDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class EntityConfigLoaderTest {

  private EntityPropertyFirstAppearanceDto registrationColumn(String id) {
    return new EntityPropertyFirstAppearanceDto(new ColumnDto(
        id,
        ColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null
    ),
        "source"
    );
  }

  private EntityPropertyTotalDto totalColumn(String id) {
    return new EntityPropertyTotalDto(new ColumnDto(
        id,
        ColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null),
        "t", EntityPropertyTotalDto.Function.SUM
    );
  }

  private EntityPropertyActionDto userActionColumn(String id) {
    return new EntityPropertyActionDto(new ColumnDto(
        id,
        ColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null),
        "registration", "l", "d", "c", true
    );
  }

  private EntityPropertySlidingWindowDto slidingWindowColumn(String id, String sourceProperty) {
    return new EntityPropertySlidingWindowDto(new ColumnDto(
        id,
        ColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null),
        sourceProperty, EntityPropertySlidingWindowDto.Function.AVG, -5, 0
    );
  }

  private ColumnComputedDto computedColumn(String id) {
    return new ColumnComputedDto(new ColumnDto(
        id,
        ColumnDto.DataType.STRING,
        null,
        null,
        null,
        null,
        null),
        "source"
    );
  }

  private EntityLogicalTable fromColumnsAndKpis(List<EntityPropertiesDto> columnsDtos,
                                                List<EntityKpisDto> kpisDtos) throws IOException {

    var firstAppearanceActionLogicalTable = new FirstAppearanceActionLogicalTable(
        "registration", "", Optional.empty(),
        TableReference.parse("app.registration"),
        new Columns(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(TemporalLogicalTable.DATE_COLUMN_TAG),
            "event_timestamp", Column.ofHidden("event_timestamp", DataType.DATETIME).withTag(
                EventLikeLogicalTable.TIMESTAMP_COLUMN_TAG),
            "unique_id", Column.ofHidden("unique_id", DataType.STRING)
                .withTag(ActionLogicalTable.ENTITY_ID_COLUMN_TAG)
        ))),
        Map.of(), Set.of(FirstAppearanceActionLogicalTable.TAG));
    var activityLogicalTable = new ActivityLogicalTable(
        "activity", "", Optional.empty(),
        TableReference.parse("app.activity"),
        new Columns(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(TemporalLogicalTable.DATE_COLUMN_TAG),
            "event_timestamp", Column.ofHidden("event_timestamp", DataType.DATETIME).withTag(
                EventLikeLogicalTable.TIMESTAMP_COLUMN_TAG),
            "unique_id", Column.ofHidden("unique_id", DataType.STRING)
                .withTag(ActionLogicalTable.ENTITY_ID_COLUMN_TAG)
        ))),
        Map.of(), Set.of(ActivityLogicalTable.TAG));

    var configLoader = new ConfigLoader(new TestConfigParser(
        Map.of(),
        Map.of(),
        Map.of(),
        Optional.of(new EntityDto(
            new EntityConfigDto("{app_id}.table"),
            columnsDtos,
            kpisDtos,
            Map.of("registration", firstAppearanceActionLogicalTable, "activity",
                activityLogicalTable)))));

    return configLoader.parse("app").getEntityLogicalTable().get();
  }

  @Test
  void shouldLoadEntityAndKpi_whenSingleKpi() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi",
                "label",
                "description",
                "category",
                true,
                "1",
                null,
                null,
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals("app", ds.getTable().schemaName().orElse(""));
    assertEquals("table_totals", ds.getTable().tableName());
    assertEquals(DataType.DATE, ds.getColumns().column("registration_date").getDataType());
    assertEquals("label", ds.kpi("kpi").label());
    assertEquals("1", ds.kpi("kpi").xaxisConfig().get("date").formula());
  }

  @Test
  void shouldFail_whenNoKpis() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(),
        List.of());
    assertEquals(0, ds.getKpis().size());
  }

  @Test
  void shouldFail_whenDuplicatePropertiesOfSameTypeInSameGroup() {
    assertThrows(IllegalArgumentException.class, () -> {
      fromColumnsAndKpis(List.of(
              new EntityPropertiesDto(List.of(
                  registrationColumn("c1"),
                  registrationColumn("c1")
              ),
                  List.of(),
                  List.of(),
                  List.of(),
                  List.of()
              )),
          List.of());
    });
  }

  @Test
  void shouldFail_whenDuplicatePropertiesOfDifferentTypeInSameGroup() {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(List.of(
                new EntityPropertiesDto(
                    List.of(registrationColumn("c1")),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(computedColumn("c1")))),
            List.of()));
  }

  @Test
  void shouldFail_whenDuplicatePropertiesOfDifferentTypeInDifferentGroups() {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(List.of(
                new EntityPropertiesDto(
                    List.of(registrationColumn("c1")),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of()),
                new EntityPropertiesDto(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(computedColumn("c1")))),
            List.of()));
  }

  @Test
  void shouldLoadProperties_whenNoDuplicatesInDifferentGroups() throws IOException {
    var ds = fromColumnsAndKpis(List.of(
            new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(), // TODO
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            ),

            new EntityPropertiesDto(
                List.of(registrationColumn("r2")),
                List.of(userActionColumn("ua2")),
                List.of(), // TODO
                List.of(totalColumn("t2")),
                List.of(computedColumn("c2"))
            )
        ),
        List.of());

    assertEquals(16, ds.getColumns().getColumns().size());
    assertEquals(Set.of(
            "r1", "r2", "ua1", "ua2", "t1", "t2", "c1", "c2",
            "registration_date", "date_", "unique_id", "last_login_date", "_dau_date", "cohort_day",
            "days_since_last_active", "cohort_size"),
        ds.getColumns().getColumns().keySet());
  }

  @Test
  void shouldLoadKpi_whenItDependsOnProperties() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(registrationColumn("r1")),
            List.of(userActionColumn("ua1")),
            List.of(),
            List.of(totalColumn("t1")),
            List.of(computedColumn("c1"))
        )),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi",
                "label",
                "description",
                "category",
                true,
                "SUM({property.r1}) + SUM({property.ua1}) + SUM({property.t1}) + SUM({property.c1})",
                null,
                null,
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals(1, ds.kpi("kpi").xaxisConfig().size());
    assertEquals(
        "SUM({r1_1}) + SUM({ua1_0}) + SUM({t1_3}) + SUM({c1_2})"
        , ds.kpi("kpi").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_1",
        new KpiComponent("{r1}", new TreeSet<>()),
        "ua1_0",
        new KpiComponent("{ua1}", new TreeSet<>(Set.of("{days_since_last_active} = 0"))),
        "t1_3",
        new KpiComponent("{t1}", new TreeSet<>()),
        "c1_2",
        new KpiComponent("{c1}", new TreeSet<>())
    ), ds.kpi("kpi").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenItReferencesPropertyInFilter() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(
                registrationColumn("r1"),
                registrationColumn("r2")
            ),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi",
                "label",
                "description",
                "category",
                true,
                "SUM({property.r1})",
                "{property.r1} = {property.r2}",
                null,
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals(1, ds.kpi("kpi").xaxisConfig().size());
    assertEquals(
        "SUM({r1_0})"
        , ds.kpi("kpi").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_0",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("{r1} = {r2}")))
    ), ds.kpi("kpi").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenColumnIsSlidingWindow() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(
            ),
            List.of(userActionColumn("ua1")),
            List.of(slidingWindowColumn("sw1", "ua1")),
            List.of(),
            List.of()
        )),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi1",
                "label",
                "description",
                "category",
                true,
                "{kpi.kpi2}",
                "{property.ua1} = 1",
                null,
                List.of("date"),
                null,
                null),
            new KpiDto(
                "kpi2",
                "label",
                "description",
                "category",
                true,
                "SUM({property.sw1})",
                "{property.sw1} = 1",
                null,
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "(SUM({sw1_0}))"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "sw1_0",
        new KpiComponent("{sw1}",
            new TreeSet<>(Set.of("{sw1} = 1", "{ua1} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(
        "SUM({sw1_1})"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "sw1_1",
        new KpiComponent("{sw1}",
            new TreeSet<>(Set.of("{sw1} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenCohortKpi() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(
                registrationColumn("r1")
            ),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi1",
                "label",
                "description",
                "category",
                true,
                "{property.r1}",
                null,
                null,
                List.of("cohort_day"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{r1_0}"
        , ds.kpi("kpi1").xaxisConfig().get("cohort_day").formula());
    assertEquals(Map.of(
        "r1_0",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("{cohort_day} IN " +
            "(0, 1, 2, 3, 4, 5, 6, 7, 14, 21, 28, 30, 40, 50, 60, 90, 120, 180, 270, 360)")))
    ), ds.kpi("kpi1").xaxisConfig().get("cohort_day").components());
  }

  @Test
  void shouldLoadKpi_whenDailyCohortedKpi() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(
                registrationColumn("r1")
            ),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )),
        List.of(new EntityKpisDto("kpis", List.of(
            new KpiDto(
                "kpi1",
                "label",
                "description",
                "category",
                true,
                "{property.r1}",
                "{property.cohort_day} = 1",
                null,
                List.of("date"),
                null,
                null),
            new KpiDto(
                "kpi2",
                "label",
                "description",
                "category",
                true,
                "{property.r1}",
                "{property.cohort_day} = 91",
                null,
                List.of("date"),
                null,
                null),
            new KpiDto(
                "kpi3",
                "label",
                "description",
                "category",
                true,
                "{property.r1}",
                "{property.cohort_day} > 3",
                null,
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{r1_0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_0",
        new KpiComponent("{r1}",
            new TreeSet<>(Set.of("{cohort_day} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals(
        "{r1_1}"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_1",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("{cohort_day} = 91",
            "{cohort_day} IN (0, 1, 2, 3, 4, 5, 6, 7, 14, 21, 28, 30, 40, 50, 60, 90, 120, 180, 270, 360)")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals(
        "{r1_2}"
        , ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_2",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("{cohort_day} > 3")))
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }

  @Test
  void shouldFail_whenItReferencesNonExistingPropertyInFilter() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({property.r1})",
                    "{property.r2}",
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenItDependsNonExistingProperty() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({property.r10})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldLoadKpi_whenItDependsOnAnotherKpis() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(registrationColumn("r1"), registrationColumn("r2"), registrationColumn("r3")),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )),
        List.of(
            new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi1",
                    "label",
                    "description",
                    "category",
                    true,
                    "{kpi.kpi2} + {kpi.kpi2} + {kpi.kpi3} - SUM({property.r3})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi2",
                    "label",
                    "description",
                    "category",
                    true,
                    "AVG({property.r2})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)
            ), List.of(), List.of()),
            new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi3",
                    "label",
                    "description",
                    "category",
                    true,
                    "MIN({property.r2}) + MAX({property.r3})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)
            ), List.of(), List.of())
        ));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "(AVG({r2_0})) + (AVG({r2_0})) + (MIN({r2_0}) + MAX({r3_1})) - SUM({r3_1})"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r2_0",
        new KpiComponent("{r2}", new TreeSet<>()),
        "r3_1",
        new KpiComponent("{r3}", new TreeSet<>())
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals("AVG({r2_0})", ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r2_0",
        new KpiComponent("{r2}", new TreeSet<>())
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals("MIN({r2_0}) + MAX({r3_1})", ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r2_0",
        new KpiComponent("{r2}", new TreeSet<>()),
        "r3_1",
        new KpiComponent("{r3}", new TreeSet<>())
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenItDependsOnAnotherKpisWithFilters() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(
            List.of(registrationColumn("r1"), registrationColumn("r2"), registrationColumn("r3")),
            List.of(),
            List.of(),
            List.of(),
            List.of()
        )),
        List.of(
            new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi1",
                    "label",
                    "description",
                    "category",
                    true,
                    "{kpi.kpi2} + SUM({property.r1})",
                    "F1",
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi2",
                    "label",
                    "description",
                    "category",
                    true,
                    "AVG({property.r1}) + {kpi.kpi3} + {kpi.kpi3}",
                    "F2",
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi3",
                    "label",
                    "description",
                    "category",
                    true,
                    "MAX({property.r1})",
                    "F3",
                    null,
                    List.of("date"),
                    null,
                    null)
            ), List.of(), List.of())
        ));

    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "(AVG({r1_1}) + (MAX({r1_0})) + (MAX({r1_0}))) + SUM({r1_2})"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_0",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F1", "F2", "F3"))),
        "r1_1",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F1", "F2"))),
        "r1_2",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F1")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals(
        "AVG({r1_4}) + (MAX({r1_3})) + (MAX({r1_3}))"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_3",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F2", "F3"))),
        "r1_4",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F2")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals(
        "MAX({r1_5})"
        , ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "r1_5",
        new KpiComponent("{r1}", new TreeSet<>(Set.of("F3")))
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }


  @Test
  void shouldFail_whenKpiLoopOf1() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenItDependsOnNonExistingKpi() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpiNonExisting})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenItDependsOnInvalidPrefix() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({invalid.kpiNonExisting})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenItDependsOnNoPrefix() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpiNonExisting})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenKpiLoopOf2() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi1",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi2})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi2",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi1})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

  @Test
  void shouldFail_whenKpiLoopOf3() throws IOException {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            )),
            List.of(new EntityKpisDto("kpis", List.of(
                new KpiDto(
                    "kpi1",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi2})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi2",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi3})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null),
                new KpiDto(
                    "kpi3",
                    "label",
                    "description",
                    "category",
                    true,
                    "SUM({kpi.kpi1})",
                    null,
                    null,
                    List.of("date"),
                    null,
                    null)),
                List.of(), List.of()))));
  }

}
