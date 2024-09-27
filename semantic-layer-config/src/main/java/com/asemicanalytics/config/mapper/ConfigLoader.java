package com.asemicanalytics.config.mapper;

import com.asemicanalytics.config.EntityModelConfig;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.mapper.dtomapper.action.ActionDtoMapper;
import com.asemicanalytics.config.mapper.dtomapper.property.EntityMapper;
import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.core.logicaltable.action.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.action.ActivityLogicalTable;
import com.asemicanalytics.core.logicaltable.action.FirstAppearanceEventLogicalTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
  private final ConfigParser configParser;

  public ConfigLoader(ConfigParser configParser) {
    this.configParser = configParser;
  }

  public Map<String, EventLogicalTable> parseActions(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    configParser.init(appId);
    return loadTopLevelLogicalTables(appId, enrichmentCollector);
  }

  public EntityModelConfig parse(String appId) throws IOException {
    List<EnrichmentDefinition> enrichmentCollector = new ArrayList<>();
    var actionLogicalTables = parseActions(appId, enrichmentCollector);
    var entity =
        new EntityMapper(appId)
            .apply(this.configParser.parseEntityLogicalTable(appId, actionLogicalTables));

    var firstAppearanceActionLogicalTable = actionLogicalTables.values().stream()
        .filter(d -> d instanceof FirstAppearanceEventLogicalTable)
        .map(d -> (FirstAppearanceEventLogicalTable) d)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No first appearance action table found"));
    var activityActionLogicalTable = actionLogicalTables.values().stream()
        .filter(d -> d instanceof ActivityLogicalTable)
        .map(d -> (ActivityLogicalTable) d)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No activity action table found"));
    return new EntityModelConfig(actionLogicalTables, entity, firstAppearanceActionLogicalTable,
        activityActionLogicalTable, enrichmentCollector);
  }

  private Map<String, EventLogicalTable> loadTopLevelLogicalTables(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    Map<String, EventLogicalTable> logicalTables = new HashMap<>();
    this.configParser.parseActionLogicalTables(appId)
        .forEach((k, v) -> logicalTables.put(k,
            new ActionDtoMapper(k, appId, enrichmentCollector).apply(v)));
    return logicalTables;
  }
}
