package com.asemicanalytics.config.configloader;

import com.asemicanalytics.config.configparser.ConfigParser;
import com.asemicanalytics.config.configparser.UserWideDatasourceDto;
import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserActionDatasourceDto;
import java.util.Map;
import java.util.Optional;

public class TestConfigParser implements ConfigParser {

  private final Map<String, StaticDatasourceDto> staticDatasources;
  private final Map<String, UserActionDatasourceDto> userActionDatasources;
  private final Map<String, CustomDailyDatasourceDto> customDailyDatasources;
  private final Optional<UserWideDatasourceDto> userWideDatasourceDto;

  public TestConfigParser(Map<String, StaticDatasourceDto> staticDatasources,
                          Map<String, UserActionDatasourceDto> userActionDatasources,
                          Map<String, CustomDailyDatasourceDto> customDailyDatasources,
                          Optional<UserWideDatasourceDto> userWideDatasourceDto) {
    this.staticDatasources = staticDatasources;
    this.userActionDatasources = userActionDatasources;
    this.customDailyDatasources = customDailyDatasources;
    this.userWideDatasourceDto = userWideDatasourceDto;
  }

  @Override
  public void init(String appId) {

  }

  @Override
  public Map<String, StaticDatasourceDto> parseStaticDatasources(String appId) {
    return staticDatasources;
  }

  @Override
  public Map<String, UserActionDatasourceDto> parseUserActionDatasources(String appId) {
    return userActionDatasources;
  }

  @Override
  public Map<String, CustomDailyDatasourceDto> parseCustomDailyDatasources(String appId) {
    return customDailyDatasources;
  }

  @Override
  public Optional<UserWideDatasourceDto> parseUserWideDatasource(
      String appId, Map<String, UserActionDatasource> userActionDatasources) {
    return userWideDatasourceDto;
  }
}
