package com.asemicanalytics.config.configloader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnComputedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ColumnDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StaticDatasourceConfigLoaderTest {
  @Test
  void testSimple() throws IOException {
    var configLoader = new ConfigLoader(new TestConfigParser(
        Map.of(
            "ds", new StaticDatasourceDto(
                "{app_id}.table",
                "Custom Label",
                null,
                List.of(new ColumnDto(
                    "col1",
                    ColumnDto.DataType.STRING,
                    null,
                    null,
                    null,
                    null,
                    null)),
                List.of())
        ),
        Map.of(),
        Map.of(),
        Optional.empty()));
    var app = configLoader.parse("app");
    var ds = app.datasource("ds");
    assertEquals("app", ds.getTable().schemaName().orElse(""));
    assertEquals("table", ds.getTable().tableName());
    assertEquals(1, ds.getColumns().getColumns().size());
    assertEquals(DataType.STRING, ds.getColumns().column("col1").getDataType());
    assertEquals(true, ds.getColumns().column("col1").canFilter());
    assertEquals(false, ds.getColumns().column("col1").canGroupBy());
    assertEquals("Custom Label", ds.getLabel());
    assertEquals(Optional.empty(), ds.getDescription());
  }

  @Test
  void testComputedColumn() throws IOException {
    var configLoader = new ConfigLoader(new TestConfigParser(
        Map.of(
            "ds", new StaticDatasourceDto(
                "{app_id}.table",
                "Custom Label",
                null,
                List.of(),
                List.of(new ColumnComputedDto(new ColumnDto(
                    "col1",
                    ColumnDto.DataType.STRING,
                    null,
                    null,
                    null,
                    null,
                    null), "f")
                ))
        ),
        Map.of(),
        Map.of(),
        Optional.empty()));
    var app = configLoader.parse("app");
    var ds = app.datasource("ds");
    assertEquals(1, ds.getColumns().getColumns().size());
    var col = (ComputedColumn) ds.getColumns().getColumns().firstEntry().getValue();
    Assertions.assertEquals("col1", col.getId());
    Assertions.assertEquals(DataType.STRING, col.getDataType());
    Assertions.assertEquals("f", col.getFormula());
  }
}
