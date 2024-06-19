package com.asemicanalytics.config.configparser;

import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.List;
import java.util.Map;

public record EntityDto(
    EntityConfigDto config,
    List<EntityPropertiesDto> columns,
    List<EntityKpisDto> kpis,
    Map<String, ActionLogicalTable> actionLogicalTables) {
}
