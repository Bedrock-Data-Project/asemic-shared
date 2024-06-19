package com.asemicanalytics.config.configparser;

import com.asemicanalytics.core.datasource.useraction.UserActionDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpisDto;
import java.util.List;
import java.util.Map;

public record UserWideDatasourceDto(
    UserWideConfigDto config,
    List<UserWideColumnsDto> columns,
    List<UserWideKpisDto> kpis,
    Map<String, UserActionDatasource> userActionDatasources) {
}
