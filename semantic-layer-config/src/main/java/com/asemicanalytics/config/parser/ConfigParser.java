package com.asemicanalytics.config.parser;

import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EventLogicalTableDto;
import java.util.Map;

public interface ConfigParser {
  void init(String appId);

  Map<String, EventLogicalTableDto> parseActionLogicalTables(String appId);

  EntityDto parseEntityLogicalTable(
      String appId, Map<String, EventLogicalTable> userActionLogicalTables);
}
