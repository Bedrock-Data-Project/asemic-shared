package com.asemicanalytics.config.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.EventLikeLogicalTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceActionLogicalTable;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyActionDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyTotalDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertyFirstAppearanceDto;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        "registration", "l", "d", "c", 1, List.of(0, 0), "MAX", true
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
            Map.of("registration", firstAppearanceActionLogicalTable, "activity", activityLogicalTable)))));

    return configLoader.parse("app").getEntityLogicalTable().get();
  }

  @Test
  void testSimple() throws IOException {
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
                List.of("date"),
                null,
                null)),
            List.of(), List.of())));
    assertEquals("app", ds.getTable().schemaName().orElse(""));
    assertEquals("table_1d", ds.getTable().tableName());
    assertEquals(DataType.DATE, ds.getColumns().column("registration_date").getDataType());
    assertEquals("label", ds.kpi("kpi").label());
  }

  @Test
  void testNoKpisIsValid() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(),
        List.of());
    assertEquals(0, ds.getKpis().size());
  }

  @Test
  void testDuplicateFirstAppearanceColumnInSameFile() {
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
  void testDuplicateDifferentTypeColumnsInSameFile() {
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
  void testDuplicateDifferentTypeColumnsInDifferentFiles() {
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
  void testValidColumnsInMultipleFiles() throws IOException {
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

    assertEquals(15, ds.getColumns().getColumns().size());
    assertEquals(Set.of(
            "r1", "r2", "ua1", "ua2", "t1", "t2", "c1", "c2",
            "registration_date", "date_", "unique_id", "last_login_date", "_dau_date", "cohort_day",
            "cohort_size"),
        ds.getColumns().getColumns().keySet());
  }

}
