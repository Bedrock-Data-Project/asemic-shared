package com.asemicanalytics.config.parser;

import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EventLogicalTableDto;
import java.util.Map;

public interface ConfigParser {
  void init(String appId);

  Map<String, EventLogicalTableDto> parseEventLogicalTables(String appId);

  EntityDto parseEntityLogicalTable(
      String appId, EventLogicalTables usereventLogicalTables);
}
