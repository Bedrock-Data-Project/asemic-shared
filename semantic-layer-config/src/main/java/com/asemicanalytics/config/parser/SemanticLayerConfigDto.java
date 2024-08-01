package com.asemicanalytics.config.parser;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.ActionLogicalTableDto;
import java.util.Map;
import java.util.Optional;

public record SemanticLayerConfigDto(
    Map<String, ActionLogicalTableDto> actionLogicalTables,
    Optional<EntityDto> entityLogicalTable) {
}
