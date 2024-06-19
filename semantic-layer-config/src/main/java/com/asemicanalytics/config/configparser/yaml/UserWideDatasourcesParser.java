package com.asemicanalytics.config.configparser.yaml;

import com.asemicanalytics.config.configparser.UserWideDatasourceDto;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpisDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserWideDatasourcesParser {
  private static final String USERWIDE_DIR = "userwide";
  private static final String CONFIG_FILE = "config.yml";
  private static final String COLUMNS_DIR = "columns";
  private static final String KPIS_DIR = "kpis";
  private final YamlFileLoader yamlFileLoader;

  public UserWideDatasourcesParser(YamlFileLoader yamlFileLoader) {
    this.yamlFileLoader = yamlFileLoader;
  }

  private List<UserWideColumnsDto> loadColumns(Path path) {
    List<UserWideColumnsDto> columns = new ArrayList<>();

    for (File file : new File(path.toUri()).listFiles()) {
      try {
        columns.add(
            yamlFileLoader.load(Files.readString(file.toPath()), UserWideColumnsDto.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return columns;
  }

  private List<UserWideKpisDto> loadKpis(Path path) {
    List<UserWideKpisDto> kpis = new ArrayList<>();

    for (File file : new File(path.toUri()).listFiles()) {
      try {
        kpis.add(yamlFileLoader.load(Files.readString(file.toPath()), UserWideKpisDto.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return kpis;
  }

  public Optional<UserWideDatasourceDto> parse(
      String appId, Path path, Map<String, UserActionDatasource> userActionDatasources) {

    var userWidePath = path.resolve(USERWIDE_DIR);
    if (!userWidePath.toFile().exists()) {
      return Optional.empty();
    }

    var configPath = userWidePath.resolve(CONFIG_FILE);
    if (!configPath.toFile().exists() || configPath.toFile().isDirectory()) {
      throw new IllegalArgumentException("UserWide requires config.yml file");
    }
    UserWideConfigDto configDto = null;
    try {
      configDto = yamlFileLoader.load(Files.readString(configPath), UserWideConfigDto.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    var columnsPath = userWidePath.resolve(COLUMNS_DIR);
    if (!columnsPath.toFile().exists() || columnsPath.toFile().isFile()) {
      throw new IllegalArgumentException("UserWide requires columns directory");
    }
    var kpisPath = userWidePath.resolve(KPIS_DIR);
    if (!kpisPath.toFile().exists() || kpisPath.toFile().isFile()) {
      throw new IllegalArgumentException("UserWide requires kpis directory");
    }

    return Optional.of(new UserWideDatasourceDto(
        configDto,
        loadColumns(columnsPath),
        loadKpis(kpisPath),
        userActionDatasources
    ));
  }

}
