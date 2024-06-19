package com.asemicanalytics.config.configparser.yaml;

import com.asemicanalytics.config.configparser.ConfigParser;
import com.asemicanalytics.config.configparser.UserWideDatasourceDto;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserActionDatasourceDto;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class YamlConfigParser implements ConfigParser {
  private final Path appsPath;
  private final YamlFileLoader yamlFileLoader;

  public YamlConfigParser(YamlFileLoader fileLoader, Path appsPath) {
    this.appsPath = appsPath;
    this.yamlFileLoader = fileLoader;
  }

  private <T> Map<String, T> readTopLevelDatasource(String appId, String prefix, Class<T> clazz) {
    Map<String, T> datasources = new HashMap<>();

    Arrays.stream(appsPath.resolve(appId).toFile().listFiles())
        .filter(file -> file.getName().startsWith(prefix))
        .forEach(file -> {
          try {
            String[] tokens = file.getName().split("\\.");
            if (tokens.length != 4) {
              throw new IllegalArgumentException("Invalid datasource file name: " + file.getName());
            }
            String datasourceId = tokens[2];

            // TODO validate using json schema
            datasources.put(
                datasourceId, yamlFileLoader.load(Files.readString(file.toPath()), clazz));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });

    return datasources;
  }

  @Override
  public void init(String appId) {
    if (!appsPath.resolve(appId).toFile().isDirectory()) {
      throw new IllegalArgumentException("App config not found: " + appId);
    }
  }

  @Override
  public Map<String, StaticDatasourceDto> parseStaticDatasources(String appId) {
    return readTopLevelDatasource(appId, "ds.static", StaticDatasourceDto.class);
  }

  @Override
  public Map<String, UserActionDatasourceDto> parseUserActionDatasources(String appId) {
    return readTopLevelDatasource(appId, "ds.user_action", UserActionDatasourceDto.class);
  }

  @Override
  public Map<String, CustomDailyDatasourceDto> parseCustomDailyDatasources(String appId) {
    return readTopLevelDatasource(appId, "ds.custom_daily", CustomDailyDatasourceDto.class);
  }

  @Override
  public Optional<UserWideDatasourceDto> parseUserWideDatasource(
      String appId, Map<String, UserActionDatasource> userActionDatasources) {
    return new UserWideDatasourcesParser(yamlFileLoader)
        .parse(appId, appsPath.resolve(appId), userActionDatasources);
  }
}
