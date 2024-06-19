package com.asemicanalytics.config.configparser.yaml;

import com.asemicanalytics.config.configparser.ConfigParser;
import com.asemicanalytics.config.configparser.EntityDto;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticLogicalTableDto;
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

  private <T> Map<String, T> readTopLevelLogicalTables(String appId,
                                                       Class<T> clazz) {
    Map<String, T> logicalTables = new HashMap<>();

    Arrays.stream(
            appsPath
                .resolve(appId)
                .resolve("userentity")
                .resolve("actions").toFile().listFiles())
        .forEach(file -> {
          try {
            String[] tokens = file.getName().split("\\.");
            if (tokens.length != 2) {
              throw new IllegalArgumentException(
                  "Invalid logical table file name: " + file.getName());
            }
            String logicalTableId = tokens[0];
            logicalTables.put(
                logicalTableId, yamlFileLoader.load(Files.readString(file.toPath()), clazz));
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });

    return logicalTables;
  }

  @Override
  public void init(String appId) {
    if (!appsPath.resolve(appId).toFile().isDirectory()) {
      throw new IllegalArgumentException("App config not found: " + appId);
    }
  }

  @Override
  public Map<String, StaticLogicalTableDto> parseStaticLogicalTables(String appId) {
    return readTopLevelLogicalTables(appId, StaticLogicalTableDto.class);
  }

  @Override
  public Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId) {
    return readTopLevelLogicalTables(appId, ActionLogicalTableDto.class);
  }

  @Override
  public Map<String, CustomDailyLogicalTableDto> parseCustomDailyLogicalTables(String appId) {
    return readTopLevelLogicalTables(appId, CustomDailyLogicalTableDto.class);
  }

  @Override
  public Optional<EntityDto> parseEntityLogicalTable(
      String appId, Map<String, ActionLogicalTable> userActionLogicalTables) {
    return new EntityLogicalTableParser(yamlFileLoader)
        .parse(appsPath.resolve(appId).resolve("userentity"), userActionLogicalTables);
  }
}
