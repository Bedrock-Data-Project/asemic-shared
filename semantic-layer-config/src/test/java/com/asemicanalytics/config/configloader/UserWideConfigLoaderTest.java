package com.asemicanalytics.config.configloader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.config.configparser.UserWideDatasourceDto;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.EventLikeDatasource;
import com.asemicanalytics.core.datasource.TemporalDatasource;
import com.asemicanalytics.core.datasource.useraction.ActivityUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.RegistrationUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnRegistrationDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnTotalDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnUserActionDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpisDto;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserWideConfigLoaderTest {

  private UserWideColumnRegistrationDto registrationColumn(String id) {
    return new UserWideColumnRegistrationDto(new ColumnDto(
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

  private UserWideColumnTotalDto totalColumn(String id) {
    return new UserWideColumnTotalDto(new ColumnDto(
        id,
        ColumnDto.DataType.DATE,
        null,
        null,
        null,
        null,
        null),
        "t", "l"
    );
  }

  private UserWideColumnUserActionDto userActionColumn(String id) {
    return new UserWideColumnUserActionDto(new ColumnDto(
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

  private UserWideDatasource fromColumnsAndKpis(List<UserWideColumnsDto> columnsDtos,
                                                List<UserWideKpisDto> kpisDtos) throws IOException {

    var registrationDatasource = new RegistrationUserActionDatasource(
        "registration", "", Optional.empty(),
        TableReference.parse("app.registration"),
        new Columns(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(TemporalDatasource.DATE_COLUMN_TAG),
            "event_timestamp", Column.ofHidden("event_timestamp", DataType.DATETIME).withTag(
                EventLikeDatasource.TIMESTAMP_COLUMN_TAG),
            "unique_id", Column.ofHidden("unique_id", DataType.STRING)
                .withTag(UserActionDatasource.USER_ID_COLUMN_TAG)
        ))),
        Map.of(), Set.of(RegistrationUserActionDatasource.DATASOURCE_TAG));
    var activityDatasource = new ActivityUserActionDatasource(
        "activity", "", Optional.empty(),
        TableReference.parse("app.activity"),
        new Columns(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(TemporalDatasource.DATE_COLUMN_TAG),
            "event_timestamp", Column.ofHidden("event_timestamp", DataType.DATETIME).withTag(
                EventLikeDatasource.TIMESTAMP_COLUMN_TAG),
            "unique_id", Column.ofHidden("unique_id", DataType.STRING)
                .withTag(UserActionDatasource.USER_ID_COLUMN_TAG)
        ))),
        Map.of(), Set.of(ActivityUserActionDatasource.DATASOURCE_TAG));

    var configLoader = new ConfigLoader(new TestConfigParser(
        Map.of(),
        Map.of(),
        Map.of(),
        Optional.of(new UserWideDatasourceDto(
            new UserWideConfigDto("{app_id}.table"),
            columnsDtos,
            kpisDtos,
            Map.of("registration", registrationDatasource, "activity", activityDatasource)))));

    return configLoader.parse("app").getUserWideDatasource().get();
  }

  @Test
  void testSimple() throws IOException {
    var ds = fromColumnsAndKpis(
        List.of(),
        List.of(new UserWideKpisDto("kpis", List.of(
            new UserWideKpiDto(
                "kpi",
                "label",
                "description",
                "category",
                true,
                "1",
                new KpiSqlDto(),
                null,
                List.of("date"),
                "SUM")),
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
  void testDuplicateRegistrationColumnInSameFile() {
    assertThrows(IllegalArgumentException.class, () -> {
      fromColumnsAndKpis(List.of(
              new UserWideColumnsDto(List.of(
                  registrationColumn("c1"),
                  registrationColumn("c1")
              ),
                  List.of(),
                  List.of(),
                  List.of()
              )),
          List.of(new UserWideKpisDto("kpis", List.of(
              new UserWideKpiDto(
                  "kpi",
                  "label",
                  "description",
                  "category",
                  true,
                  "1",
                  new KpiSqlDto(),
                  null,
                  List.of("date"),
                  "SUM")),
              List.of(), List.of())));
    });
  }

  @Test
  void testDuplicateDifferentTypeColumnsInSameFile() {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(List.of(
                new UserWideColumnsDto(
                    List.of(registrationColumn("c1")),
                    List.of(),
                    List.of(),
                    List.of(computedColumn("c1")))),
            List.of(new UserWideKpisDto("kpis", List.of(
                new UserWideKpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "1",
                    new KpiSqlDto(),
                    null,
                    List.of("date"),
                    "SUM")),
                List.of(), List.of()))
        )
    );
  }

  @Test
  void testDuplicateDifferentTypeColumnsInDifferentFiles() {
    assertThrows(IllegalArgumentException.class, () ->
        fromColumnsAndKpis(List.of(
                new UserWideColumnsDto(
                    List.of(registrationColumn("c1")),
                    List.of(),
                    List.of(),
                    List.of()),
                new UserWideColumnsDto(
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(computedColumn("c1")))),
            List.of(new UserWideKpisDto("kpis", List.of(
                new UserWideKpiDto(
                    "kpi",
                    "label",
                    "description",
                    "category",
                    true,
                    "1",
                    new KpiSqlDto(),
                    null,
                    List.of("date"),
                    "SUM")),
                List.of(), List.of()))
        )
    );
  }

  @Test
  void testValidColumnsInMultipleFiles() throws IOException {
    var ds = fromColumnsAndKpis(List.of(
            new UserWideColumnsDto(
                List.of(registrationColumn("r1")),
                List.of(userActionColumn("ua1")),
                List.of(totalColumn("t1")),
                List.of(computedColumn("c1"))
            ),

            new UserWideColumnsDto(
                List.of(registrationColumn("r2")),
                List.of(userActionColumn("ua2")),
                List.of(totalColumn("t2")),
                List.of(computedColumn("c2"))
            )
        ),
        List.of(new UserWideKpisDto("kpis", List.of(
            new UserWideKpiDto(
                "kpi",
                "label",
                "description",
                "category",
                true,
                "1",
                new KpiSqlDto(),
                null,
                List.of("date"),
                "SUM")),
            List.of(), List.of()))
    );

    assertEquals(15, ds.getColumns().getColumns().size());
    assertEquals(Set.of(
            "r1", "r2", "ua1", "ua2", "t1", "t2", "c1", "c2",
            "registration_date", "date_", "unique_id", "last_login_date", "_dau_date", "cohort_day",
            "cohort_size"),
        ds.getColumns().getColumns().keySet());
  }

}
