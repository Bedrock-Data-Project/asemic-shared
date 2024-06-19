package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;

public class StaticDatasourceDtoMapper implements Function<StaticDatasourceDto, Datasource> {
  private final String datasourceId;
  private final String appId;

  public StaticDatasourceDtoMapper(String datasourceId, String appId) {
    this.datasourceId = datasourceId;
    this.appId = appId;
  }

  @Override
  public Datasource apply(StaticDatasourceDto dto) {
    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    dto.getColumns().stream().map(new ColumnDtoMapper())
        .forEach(c -> columns.put(c.getId(), c));
    dto.getComputedColumns().ifPresent(cols ->
        cols.stream()
            .map(new ColumnComputedDtoMapper())
            .forEach(c -> {
              if (columns.put(c.getId(), c) != null) {
                throw new IllegalArgumentException(
                    "Duplicate column id: " + c.getId() + " in datasource: " + datasourceId);
              }
            }));

    return new Datasource(
        datasourceId,
        DefaultLabel.of(dto.getLabel(), datasourceId),
        dto.getDescription(),
        TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
        new Columns(columns),
        Set.of()
    );
  }
}
