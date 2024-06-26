package com.asemicanalytics.config.parser;

import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticLogicalTableDto;
import java.util.Map;
import java.util.Optional;

public interface ConfigParser {
  void init(String appId);

  Map<String, StaticLogicalTableDto> parseStaticLogicalTables(String appId);

  Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId);

  Map<String, CustomDailyLogicalTableDto> parseCustomDailyLogicalTables(String appId);

  Optional<EntityDto> parseEntityLogicalTable(
      String appId, Map<String, ActionLogicalTable> userActionLogicalTables);
}
