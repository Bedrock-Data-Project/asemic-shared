package com.asemicanalytics.config.mapper;

import com.asemicanalytics.config.EntityModelConfig;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.config.mapper.dtomapper.event.EventDtoMapper;
import com.asemicanalytics.config.mapper.dtomapper.property.EntityMapper;
import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
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

  public EventLogicalTables parseEvents(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    configParser.init(appId);
    return loadTopLevelLogicalTables(appId, enrichmentCollector);
  }

  public EntityModelConfig parse(String appId) throws IOException {
    List<EnrichmentDefinition> enrichmentCollector = new ArrayList<>();
    var eventLogicalTables = parseEvents(appId, enrichmentCollector);
    var entity =
        new EntityMapper(appId)
            .apply(this.configParser.parseEntityLogicalTable(appId, eventLogicalTables));

    return new EntityModelConfig(eventLogicalTables, entity,
        entity.getFirstAppearanceActionLogicalTable(),
        entity.getActivityLogicalTable(),
        enrichmentCollector);
  }

  private EventLogicalTables loadTopLevelLogicalTables(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    Map<String, EventLogicalTable> logicalTables = new HashMap<>();
    this.configParser.parseeventLogicalTables(appId)
        .forEach((k, v) -> logicalTables.put(k,
            new EventDtoMapper(k, appId, enrichmentCollector).apply(v)));
    return new EventLogicalTables(logicalTables);
  }
}
