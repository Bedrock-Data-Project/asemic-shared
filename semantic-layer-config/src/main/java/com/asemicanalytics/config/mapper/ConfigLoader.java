package com.asemicanalytics.config.mapper;

import com.asemicanalytics.config.EntityModelConfig;
import com.asemicanalytics.config.mapper.dtomapper.ActionDtoMapper;
import com.asemicanalytics.config.mapper.dtomapper.entity.EntityMapper;
import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
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

  public EntityModelConfig parse(String appId) throws IOException {
    configParser.init(appId);
    List<EnrichmentDefinition> enrichmentCollector = new ArrayList<>();
    var actionLogicalTables = loadTopLevelLogicalTables(appId, enrichmentCollector);
    var entity = this.configParser.parseEntityLogicalTable(appId, actionLogicalTables)
        .map(dto -> new EntityMapper(appId).apply(dto));
    return new EntityModelConfig(actionLogicalTables, entity, enrichmentCollector);
  }

  private Map<String, ActionLogicalTable> loadTopLevelLogicalTables(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    Map<String, ActionLogicalTable> logicalTables = new HashMap<>();
    this.configParser.parseActionLogicalTables(appId)
        .forEach((k, v) -> logicalTables.put(k,
            new ActionDtoMapper(k, appId, enrichmentCollector).apply(v)));
    return logicalTables;
  }
}
