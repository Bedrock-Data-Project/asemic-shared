package com.asemicanalytics.config.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.logicaltable.EventLikeLogicalTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceEventLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionColumnDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFirstAppearanceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyLifetimeDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertySlidingWindowDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.PropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.XAxisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.XaxisOverrideDto;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class EntityConfigLoaderTest {

  private EntityPropertyDto registrationColumn() {
    return new EntityPropertyDto(
        null,
        ActionColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        new EntityPropertyFirstAppearanceDto("source"),
        null);
  }

  private EntityPropertyDto lifetimeColumn() {
    return new EntityPropertyDto(
        null,
        ActionColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null,
        null,
        new EntityPropertyLifetimeDto("t", null, null, EntityPropertyLifetimeDto.MergeFunction.SUM),
        null,
        null);

  }

  private EntityPropertyDto actionColumn() {
    return new EntityPropertyDto(
        null,
        ActionColumnDto.DataType.DATE,
        null,
        null,
        null,
        new EntityPropertyActionDto(
            "registration",
            "{l}",
            EntityPropertyActionDto.AggregateFunction.SUM,
            "{l}",
            null
        ),
        null,
        null,
        null,
        null,
        null);
  }

  private EntityPropertyDto slidingWindowColumn(String sourceProperty) {
    return new EntityPropertyDto(
        null,
        ActionColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        new EntityPropertySlidingWindowDto(
            sourceProperty,
            null,
            null,
            EntityPropertySlidingWindowDto.EntityPropertyWindowFunction.AVG,
            -5,
            0
        ),
        null,
        null,
        null,
        null);
  }

  private EntityPropertyDto computedColumn() {
    return new EntityPropertyDto(
        null,
        ActionColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        new EntityPropertyComputedDto("source", List.of()));
  }

  private KpiDto kpi(String select, Optional<String> where, List<String> xaxis) {
    var xaxisDto = new XAxisDto();
    xaxis.forEach(x -> xaxisDto.setAdditionalProperty(x, new XaxisOverrideDto(null)));

    return new KpiDto(
        "label",
        "description",
        select,
        where.orElse(null),
        null,
        null,
        xaxisDto,
        null
    );
  }

  private EntityLogicalTable fromColumnsAndKpis(List<EntityPropertiesDto> columnsDtos,
                                                List<EntityKpisDto> kpisDtos) throws IOException {

    var firstAppearanceActionLogicalTable = new ActionLogicalTableDto(
        "app.registration", List.of(FirstAppearanceEventLogicalTable.TAG),
        null, null,
        new ColumnsDto() {{
          setAdditionalProperty("date_",
              new ActionColumnDto(ActionColumnDto.DataType.DATE, null, null,
                  List.of(TemporalLogicalTable.DATE_COLUMN_TAG)));
          setAdditionalProperty("event_timestamp",
              new ActionColumnDto(ActionColumnDto.DataType.DATETIME, null, null,
                  List.of(EventLikeLogicalTable.TIMESTAMP_COLUMN_TAG)));
          setAdditionalProperty("unique_id",
              new ActionColumnDto(ActionColumnDto.DataType.STRING, null, null,
                  List.of(EventLogicalTable.ENTITY_ID_COLUMN_TAG)));
        }}, null, List.of());

    var activityLogicalTable = new ActionLogicalTableDto(
        "app.activity", List.of(ActivityLogicalTable.TAG),
        null, null,
        new ColumnsDto() {{
          setAdditionalProperty("date_",
              new ActionColumnDto(ActionColumnDto.DataType.DATE, null, null,
                  List.of(TemporalLogicalTable.DATE_COLUMN_TAG)));
          setAdditionalProperty("event_timestamp",
              new ActionColumnDto(ActionColumnDto.DataType.DATETIME, null, null,
                  List.of(EventLikeLogicalTable.TIMESTAMP_COLUMN_TAG)));
          setAdditionalProperty("unique_id",
              new ActionColumnDto(ActionColumnDto.DataType.STRING, null, null,
                  List.of(EventLogicalTable.ENTITY_ID_COLUMN_TAG)));
        }}, null, List.of());

    var configLoader = new ConfigLoader(new TestConfigParser(
        Map.of("registration", firstAppearanceActionLogicalTable, "activity", activityLogicalTable),
        columnsDtos, kpisDtos));

    return configLoader.parse("app").getEntityLogicalTable();
  }

  @Test
  void shouldFail_whenNoKpis() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(new KpisDto())));
    assertEquals(0, ds.getKpis().size());
  }

  @ParameterizedTest
  @CsvSource({
      "aggregation in aggregation, SUM(SUM({property.r1}))",
      "no aggregation, {property.r1} + {property.r2}",
      "partial no aggregation, SUM({property.r1}) + {property.r2}",
      "non aggregation function, _SUM({property.r1})",
      "non matching bracket count, SUM({property.r1}))",
      "matching but invalid bracket order, SUM({property.r1}))(",
  })
  void shouldFail_whenAggregationIsMalformed(String testType, String formula) {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("r2", registrationColumn());
    propertiesDto.setAdditionalProperty("r3", registrationColumn());


    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi", kpi(formula, Optional.empty(), List.of("date")));
    assertThrows(IllegalArgumentException.class, () ->
            fromColumnsAndKpis(
                List.of(new EntityPropertiesDto(propertiesDto)),
                List.of(new EntityKpisDto(kpisDto))),
        testType
    );
  }

  @Test
  void shouldFail_whenDuplicatePropertiesOfDifferentTypeInDifferentGroups() {
    var propertiesDto1 = new PropertiesDto();
    propertiesDto1.setAdditionalProperty("r1", registrationColumn());

    var propertiesDto2 = new PropertiesDto();
    propertiesDto2.setAdditionalProperty("r1", computedColumn());

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(List.of(
                new EntityPropertiesDto(propertiesDto1),
                new EntityPropertiesDto(propertiesDto2)),
            List.of()));
  }

  @Test
  void shouldLoadProperties_whenNoDuplicatesInDifferentGroups() throws IOException {
    var propertiesDto1 = new PropertiesDto();
    propertiesDto1.setAdditionalProperty("r1", registrationColumn());
    propertiesDto1.setAdditionalProperty("ua1", actionColumn());
    propertiesDto1.setAdditionalProperty("sl1", slidingWindowColumn("ua1"));
    propertiesDto1.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto1.setAdditionalProperty("c1", computedColumn());

    var propertiesDto2 = new PropertiesDto();
    propertiesDto2.setAdditionalProperty("r2", registrationColumn());
    propertiesDto2.setAdditionalProperty("ua2", actionColumn());
    propertiesDto2.setAdditionalProperty("sl2", slidingWindowColumn("ua2"));
    propertiesDto2.setAdditionalProperty("t2", lifetimeColumn());
    propertiesDto2.setAdditionalProperty("c2", computedColumn());

    var ds = fromColumnsAndKpis(List.of(
            new EntityPropertiesDto(propertiesDto1),
            new EntityPropertiesDto(propertiesDto2)
        ),
        List.of());

    assertEquals(21, ds.getColumns().getColumns().size());
    assertEquals(Set.of(
            "r1", "r2", "ua1", "ua2", "t1", "t1__inner", "t2", "t2__inner", "c1", "c2", "sl1",
            "sl1__inner", "sl2", "sl2__inner",
            "first_appearance_date", "date_", "unique_id", "last_login_date", "cohort_day",
            "days_since_last_active", "cohort_size"),
        ds.getColumns().getColumns().keySet());
  }

  @Test
  void shouldLoadKpi_whenItDependsOnProperties() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("sl1", slidingWindowColumn("ua1"));
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({property.r1}) + SUM({property.ua1}) + SUM({property.t1}) + SUM({property.c1})",
            Optional.empty(), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi").xaxisConfig().size());
    assertEquals(
        "{component0} + {component1} + {component2} + {component3}"
        , ds.kpi("kpi").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r1})", new TreeSet<>()),
        "component1",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of("{days_since_last_active} = 0"))),
        "component2",
        new KpiComponent("SUM({t1})", new TreeSet<>()),
        "component3",
        new KpiComponent("SUM({c1})", new TreeSet<>())
    ), ds.kpi("kpi").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadBothAxisKpi_whenItDependsOnProperties() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("ua1", actionColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({property.ua1})",
            Optional.empty(), List.of("date", "cohort_day")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(2, ds.kpi("kpi").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of("{days_since_last_active} = 0")))
    ), ds.kpi("kpi").xaxisConfig().get("date").components());

    assertEquals(
        "{component0}"
        , ds.kpi("kpi").xaxisConfig().get("cohort_day").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of(
            "{cohort_day} IN (1, 2)")))
    ), ds.kpi("kpi").xaxisConfig().get("cohort_day").components());
  }

  @Test
  void shouldLoadBothAxisKpi_whenItDependsOnKpiBothAxis() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("ua1", actionColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("{kpi.kpi2}",
            Optional.empty(), List.of("date", "cohort_day")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("SUM({property.ua1})",
            Optional.empty(), List.of("date", "cohort_day")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(2, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of("{days_since_last_active} = 0")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(
        "{component0}"
        , ds.kpi("kpi1").xaxisConfig().get("cohort_day").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of(
            "{cohort_day} IN (1, 2)")))
    ), ds.kpi("kpi1").xaxisConfig().get("cohort_day").components());

    assertEquals(2, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of("{days_since_last_active} = 0")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi2").xaxisConfig().get("cohort_day").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({ua1})", new TreeSet<>(Set.of(
            "{cohort_day} IN (1, 2)")))
    ), ds.kpi("kpi2").xaxisConfig().get("cohort_day").components());
  }

  @Test
  void shouldLoadKpi_whenItReferencesPropertyInFilter() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("r2", actionColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({property.r1})",
            Optional.of("{property.r1} = {property.r2}"), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r1})", new TreeSet<>(Set.of("{r1} = {r2}")))
    ), ds.kpi("kpi").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenColumnIsSlidingWindow() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("sw1", slidingWindowColumn("ua1"));

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("{kpi.kpi2}",
            Optional.of("{property.ua1} = 1"), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("SUM({property.sw1})",
            Optional.of("{property.sw1} = 1"), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({sw1})",
            new TreeSet<>(Set.of("{sw1} = 1", "{ua1} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(
        "{component0}"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({sw1})",
            new TreeSet<>(Set.of("{sw1} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenCohortKpi() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("AVG({property.r1})",
            Optional.empty(), List.of("cohort_day")));
    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi1").xaxisConfig().get("cohort_day").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("AVG({r1})", new TreeSet<>(Set.of("{cohort_day} IN " +
            "(1, 2)")))
    ), ds.kpi("kpi1").xaxisConfig().get("cohort_day").components());
  }

  @Test
  void shouldLoadKpi_whenDailyCohortedKpi() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("SUM({property.r1})",
            Optional.of("{property.cohort_day} = 1"), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("SUM({property.r1})",
            Optional.of("{property.cohort_day} = 91"), List.of("date")));
    kpisDto.setAdditionalProperty("kpi3",
        kpi("SUM({property.r1})",
            Optional.of("{property.cohort_day} > 3"), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r1})",
            new TreeSet<>(Set.of("{cohort_day} = 1", "{days_since_last_active} <= 90")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r1})", new TreeSet<>(Set.of("{cohort_day} = 91",
            "{cohort_day} IN (1, 2)")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r1})", new TreeSet<>(Set.of("{cohort_day} > 3")))
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }

  @Test
  void shouldFail_whenItReferencesNonExistingPropertyInFilter() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({property.r1})",
            Optional.of("{property.r2}"), List.of("date")));


    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenItDependsNonExistingProperty() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({property.r10})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldLoadKpi_whenItDependsOnAnotherKpis() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("r2", registrationColumn());
    propertiesDto.setAdditionalProperty("r3", registrationColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("{kpi.kpi2} + {kpi.kpi2} + {kpi.kpi3} - SUM({property.r3})",
            Optional.empty(), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("AVG({property.r2})",
            Optional.empty(), List.of("date")));
    kpisDto.setAdditionalProperty("kpi3",
        kpi("MIN({property.r2}) + MAX({property.r3})",
            Optional.empty(), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));
    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "({component1}) + ({component1}) + ({component2} + {component3}) - {component0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("SUM({r3})", new TreeSet<>()),
        "component1",
        new KpiComponent("AVG({r2})", new TreeSet<>()),
        "component2",
        new KpiComponent("MIN({r2})", new TreeSet<>()),
        "component3",
        new KpiComponent("MAX({r3})", new TreeSet<>())
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals("{component0}", ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("AVG({r2})", new TreeSet<>())
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals("{component0} + {component1}", ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("MIN({r2})", new TreeSet<>()),
        "component1",
        new KpiComponent("MAX({r3})", new TreeSet<>())
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }

  @Test
  void shouldLoadKpi_whenItDependsOnAnotherKpisWithFilters() throws IOException {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("r2", registrationColumn());
    propertiesDto.setAdditionalProperty("r3", registrationColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("{kpi.kpi2} + SUM({property.r1})",
            Optional.of("F1"), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("AVG({property.r1}) + {kpi.kpi3} + {kpi.kpi3}",
            Optional.of("F2"), List.of("date")));
    kpisDto.setAdditionalProperty("kpi3",
        kpi("MAX({property.r1})",
            Optional.of("F3"), List.of("date")));

    var ds = fromColumnsAndKpis(
        List.of(new EntityPropertiesDto(propertiesDto)),
        List.of(new EntityKpisDto(kpisDto)));

    assertEquals(1, ds.kpi("kpi1").xaxisConfig().size());
    assertEquals(
        "({component1} + ({component2}) + ({component2})) + {component0}"
        , ds.kpi("kpi1").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component2",
        new KpiComponent("MAX({r1})", new TreeSet<>(Set.of("F1", "F2", "F3"))),
        "component1",
        new KpiComponent("AVG({r1})", new TreeSet<>(Set.of("F1", "F2"))),
        "component0",
        new KpiComponent("SUM({r1})", new TreeSet<>(Set.of("F1")))
    ), ds.kpi("kpi1").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi2").xaxisConfig().size());
    assertEquals(
        "{component0} + ({component1}) + ({component1})"
        , ds.kpi("kpi2").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component1",
        new KpiComponent("MAX({r1})", new TreeSet<>(Set.of("F2", "F3"))),
        "component0",
        new KpiComponent("AVG({r1})", new TreeSet<>(Set.of("F2")))
    ), ds.kpi("kpi2").xaxisConfig().get("date").components());

    assertEquals(1, ds.kpi("kpi3").xaxisConfig().size());
    assertEquals(
        "{component0}"
        , ds.kpi("kpi3").xaxisConfig().get("date").formula());
    assertEquals(Map.of(
        "component0",
        new KpiComponent("MAX({r1})", new TreeSet<>(Set.of("F3")))
    ), ds.kpi("kpi3").xaxisConfig().get("date").components());
  }


  @Test
  void shouldFail_whenKpiLoopOf1() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({kpi.kpi})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenItDependsOnNonExistingKpi() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({kpi.kpiNonExisting})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenItDependsOnInvalidPrefix() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({invalid.kpiNonExisting})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenItDependsOnNoPrefix() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi",
        kpi("SUM({kpiNonExisting})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenKpiLoopOf2() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("SUM({kpi.kpi2})",
            Optional.empty(), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("SUM({kpi.kpi1})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

  @Test
  void shouldFail_whenKpiLoopOf3() {
    var propertiesDto = new PropertiesDto();
    propertiesDto.setAdditionalProperty("r1", registrationColumn());
    propertiesDto.setAdditionalProperty("ua1", actionColumn());
    propertiesDto.setAdditionalProperty("t1", lifetimeColumn());
    propertiesDto.setAdditionalProperty("c1", computedColumn());

    var kpisDto = new KpisDto();
    kpisDto.setAdditionalProperty("kpi1",
        kpi("SUM({kpi.kpi2})",
            Optional.empty(), List.of("date")));
    kpisDto.setAdditionalProperty("kpi2",
        kpi("SUM({kpi.kpi3})",
            Optional.empty(), List.of("date")));
    kpisDto.setAdditionalProperty("kpi3",
        kpi("SUM({kpi.kpi1})",
            Optional.empty(), List.of("date")));

    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(
            List.of(new EntityPropertiesDto(propertiesDto)),
            List.of(new EntityKpisDto(kpisDto))));
  }

}
