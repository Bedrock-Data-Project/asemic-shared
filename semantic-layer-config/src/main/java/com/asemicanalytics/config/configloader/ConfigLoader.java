package com.asemicanalytics.config.configloader;

import com.asemicanalytics.config.configloader.dtomapper.CustomDailyDatasourceDtoMapper;
import com.asemicanalytics.config.configloader.dtomapper.StaticDatasourceDtoMapper;
import com.asemicanalytics.config.configloader.dtomapper.UserActionDatasourceDtoMapper;
import com.asemicanalytics.config.configloader.dtomapper.UserWideDatasourceMapper;
import com.asemicanalytics.config.configparser.ConfigParser;
import com.asemicanalytics.config.enrichment.EnrichmentDefinition;
import com.asemicanalytics.core.datasource.Datasource;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
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

  public SemanticLayerConfig parse(String appId) throws IOException {
    configParser.init(appId);
    List<EnrichmentDefinition> enrichmentCollector = new ArrayList<>();
    var datasources = loadTopLevelDatasources(appId, enrichmentCollector);
    Map<String, UserActionDatasource> userActionDatasources = datasources.values().stream()
        .filter(ds -> ds instanceof UserActionDatasource)
        .map(ds -> (UserActionDatasource) ds)
        .collect(HashMap::new, (m, ds) -> m.put(ds.getId(), ds), HashMap::putAll);
    var userWide = this.configParser.parseUserWideDatasource(appId, userActionDatasources)
        .map(dto -> new UserWideDatasourceMapper(appId).apply(dto));
    return new SemanticLayerConfig(datasources, userWide, enrichmentCollector);
  }

  private Map<String, Datasource> loadTopLevelDatasources(
      String appId, List<EnrichmentDefinition> enrichmentCollector) {
    Map<String, Datasource> datasources = new HashMap<>();
    this.configParser.parseStaticDatasources(appId)
        .forEach((k, v) -> datasources.put(k, new StaticDatasourceDtoMapper(k, appId).apply(v)));
    this.configParser.parseCustomDailyDatasources(appId)
        .forEach((k, v) -> datasources.put(k,
            new CustomDailyDatasourceDtoMapper(k, appId, enrichmentCollector).apply(v)));
    this.configParser.parseUserActionDatasources(appId)
        .forEach((k, v) -> datasources.put(k,
            new UserActionDatasourceDtoMapper(k, appId, enrichmentCollector).apply(v)));
    return datasources;
  }
}
