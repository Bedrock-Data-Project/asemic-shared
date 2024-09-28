package com.asemicanalytics.config.parser;

import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import java.util.List;

public record EntityDto(
    EntityConfigDto config,
    List<EntityPropertiesDto> columns,
    List<EntityKpisDto> kpis,
    EventLogicalTables eventLogicalTables) {
}
