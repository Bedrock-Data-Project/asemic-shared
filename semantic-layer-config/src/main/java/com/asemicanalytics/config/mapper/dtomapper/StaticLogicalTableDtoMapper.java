package com.asemicanalytics.config.mapper.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.mapper.dtomapper.column.ColumnComputedDtoMapper;
import com.asemicanalytics.config.mapper.dtomapper.column.ColumnDtoMapper;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.LogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticLogicalTableDto;
import java.util.LinkedHashMap;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;

public class StaticLogicalTableDtoMapper implements Function<StaticLogicalTableDto, LogicalTable> {
  private final String id;
  private final String appId;

  public StaticLogicalTableDtoMapper(String id, String appId) {
    this.id = id;
    this.appId = appId;
  }

  @Override
  public LogicalTable apply(StaticLogicalTableDto dto) {
    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    dto.getColumns().stream().map(new ColumnDtoMapper())
        .forEach(c -> columns.put(c.getId(), c));
    dto.getComputedColumns().ifPresent(cols ->
        cols.stream()
            .map(new ColumnComputedDtoMapper())
            .forEach(c -> {
              if (columns.put(c.getId(), c) != null) {
                throw new IllegalArgumentException(
                    "Duplicate column id: " + c.getId() + " in logicalTable: " + id);
              }
            }));

    return new LogicalTable(
        id,
        DefaultLabel.of(dto.getLabel(), id),
        dto.getDescription(),
        TableReference.parse(dto.getTableName().replace("{app_id}", appId)),
        new Columns(columns),
        Set.of()
    );
  }
}
