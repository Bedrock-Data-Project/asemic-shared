package com.asemicanalytics.config.mapper;

import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.config.parser.EntityDto;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestConfigParser implements ConfigParser {

  private final Map<String, ActionLogicalTableDto> actionLogicalTables;
  private final List<EntityPropertiesDto> columnsDtos;
  private final List<EntityKpisDto> kpisDtos;

  public TestConfigParser(Map<String, ActionLogicalTableDto> actionLogicalTables,
                          List<EntityPropertiesDto> columnsDtos,
                          List<EntityKpisDto> kpisDtos) {
    this.actionLogicalTables = actionLogicalTables;
    this.columnsDtos = columnsDtos;
    this.kpisDtos = kpisDtos;
  }

  @Override
  public void init(String appId) {

  }

  @Override
  public Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId) {
    return actionLogicalTables;
  }

  @Override
  public EntityDto parseEntityLogicalTable(
      String appId, Map<String, ActionLogicalTable> userActionLogicalTables) {
    return new EntityDto(
        new EntityConfigDto("{app_id}.table", List.of(1, 2), 90),
        columnsDtos,
        kpisDtos, userActionLogicalTables.entrySet().stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, e -> userActionLogicalTables.get(e.getKey()))));
  }
}
