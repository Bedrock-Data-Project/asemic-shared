package com.asemicanalytics.config.parser;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.CustomDailyLogicalTableDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.StaticLogicalTableDto;
import java.util.Map;
import java.util.Optional;

public record SemanticLayerConfigDto(
    Map<String, StaticLogicalTableDto> staticLogicalTables,
    Map<String, ActionLogicalTableDto> actionLogicalTables,
    Map<String, CustomDailyLogicalTableDto> customDailyLogicalTables,
    Optional<EntityDto> entityLogicalTable) {
}
