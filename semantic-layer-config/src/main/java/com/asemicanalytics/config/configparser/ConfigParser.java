package com.asemicanalytics.config.configparser;

import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserActionDatasourceDto;
import java.util.Map;
import java.util.Optional;

public interface ConfigParser {
  void init(String appId);

  Map<String, StaticDatasourceDto> parseStaticDatasources(String appId);

  Map<String, UserActionDatasourceDto> parseUserActionDatasources(String appId);

  Map<String, CustomDailyDatasourceDto> parseCustomDailyDatasources(String appId);

  Optional<UserWideDatasourceDto> parseUserWideDatasource(
      String appId, Map<String, UserActionDatasource> userActionDatasources);
}
