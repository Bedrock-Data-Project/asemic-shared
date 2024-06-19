package com.asemicanalytics.config.configloader;

import com.asemicanalytics.config.configparser.ConfigParser;
import com.asemicanalytics.config.configparser.EntityDto;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticLogicalTableDto;
import java.util.Map;
import java.util.Optional;

public class TestConfigParser implements ConfigParser {

  private final Map<String, StaticLogicalTableDto> staticLogicalTables;
  private final Map<String, ActionLogicalTableDto> actionLogicalTables;
  private final Map<String, CustomDailyLogicalTableDto> customDailyLogicalTables;
  private final Optional<EntityDto> entityDto;

  public TestConfigParser(Map<String, StaticLogicalTableDto> staticLogicalTables,
                          Map<String, ActionLogicalTableDto> actionLogicalTables,
                          Map<String, CustomDailyLogicalTableDto> customDailyLogicalTables,
                          Optional<EntityDto> entityDto) {
    this.staticLogicalTables = staticLogicalTables;
    this.actionLogicalTables = actionLogicalTables;
    this.customDailyLogicalTables = customDailyLogicalTables;
    this.entityDto = entityDto;
  }

  @Override
  public void init(String appId) {

  }

  @Override
  public Map<String, StaticLogicalTableDto> parseStaticLogicalTables(String appId) {
    return staticLogicalTables;
  }

  @Override
  public Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId) {
    return actionLogicalTables;
  }

  @Override
  public Map<String, CustomDailyLogicalTableDto> parseCustomDailyLogicalTables(String appId) {
    return customDailyLogicalTables;
  }

  @Override
  public Optional<EntityDto> parseEntityLogicalTable(
      String appId, Map<String, ActionLogicalTable> userActionLogicalTables) {
    return entityDto;
  }
}
