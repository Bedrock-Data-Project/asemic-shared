package com.asemicanalytics.config.configparser;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticDatasourceDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserActionDatasourceDto;
import java.util.Map;
import java.util.Optional;

public record SemanticLayerConfigDto(
    Map<String, StaticDatasourceDto> staticDatasource,
    Map<String, UserActionDatasourceDto> userActionDatasource,
    Map<String, CustomDailyDatasourceDto> customDailyDatasource,
    Optional<UserWideDatasourceDto> userWideDatasource) {
}
