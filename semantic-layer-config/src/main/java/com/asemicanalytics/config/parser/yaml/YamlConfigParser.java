package com.asemicanalytics.config.parser.yaml;

import com.asemicanalytics.config.parser.ConfigParser;
import com.asemicanalytics.config.parser.EntityDto;
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
                                                       Class<T> clazz, Path logicalTablePath) {
    Map<String, T> logicalTables = new HashMap<>();

    if (!logicalTablePath.toFile().isDirectory()) {
      return logicalTables;
    }

    Arrays.stream(logicalTablePath.toFile().listFiles())
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
    // TODO implement
    return readTopLevelLogicalTables(appId, StaticLogicalTableDto.class, staticDir(appId));
  }

  @Override
  public Map<String, ActionLogicalTableDto> parseActionLogicalTables(String appId) {
    return readTopLevelLogicalTables(appId, ActionLogicalTableDto.class, actionsDir(appId));
  }

  @Override
  public Map<String, CustomDailyLogicalTableDto> parseCustomDailyLogicalTables(String appId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<EntityDto> parseEntityLogicalTable(
      String appId, Map<String, ActionLogicalTable> userActionLogicalTables) {
    return new EntityLogicalTableParser(yamlFileLoader)
        .parse(propertiesDir(appId), kpisDir(appId), userActionLogicalTables);
  }

  public Path actionsDir(String appId) {
    return appsPath.resolve(appId).resolve("userentity").resolve("actions");
  }

  public Path staticDir(String appId) {
    return appsPath.resolve(appId).resolve("userentity").resolve("static");
  }

  public Path propertiesDir(String appId) {
    return appsPath.resolve(appId).resolve("userentity").resolve("properties");
  }

  public Path kpisDir(String appId) {
    return appsPath.resolve(appId).resolve("userentity").resolve("kpis");
  }
}
