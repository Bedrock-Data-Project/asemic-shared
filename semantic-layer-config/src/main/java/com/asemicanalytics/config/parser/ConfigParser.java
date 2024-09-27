package com.asemicanalytics.config.parser;

import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import java.util.Map;

public interface ConfigParser {
  void init(String appId);

  Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId);

  EntityDto parseEntityLogicalTable(
      String appId, Map<String, EventLogicalTable> userActionLogicalTables);
}
