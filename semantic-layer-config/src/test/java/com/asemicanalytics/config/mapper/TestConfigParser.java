package com.asemicanalytics.config.mapper;

import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EventLogicalTableDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestConfigParser implements ConfigParser {

  private final Map<String, EventLogicalTableDto> eventLogicalTables;
  private final List<EntityPropertiesDto> columnsDtos;
  private final List<EntityKpisDto> kpisDtos;

  public TestConfigParser(Map<String, EventLogicalTableDto> eventLogicalTables,
                          List<EntityPropertiesDto> columnsDtos,
                          List<EntityKpisDto> kpisDtos) {
    this.eventLogicalTables = eventLogicalTables;
    this.columnsDtos = columnsDtos;
    this.kpisDtos = kpisDtos;
  }

  @Override
  public void init(String appId) {

  }

  @Override
  public Map<String, EventLogicalTableDto> parseEventLogicalTables(String appId) {
    return eventLogicalTables;
  }

  @Override
  public EntityDto parseEntityLogicalTable(
      String appId, EventLogicalTables usereventLogicalTables) {
    return new EntityDto(
        new EntityConfigDto("{app_id}", "table", List.of(1, 2), 90),
        columnsDtos,
        kpisDtos,
        new EventLogicalTables(
            usereventLogicalTables.getEventLogicalTables().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey, e -> usereventLogicalTables.get(e.getKey())))));
  }
}
