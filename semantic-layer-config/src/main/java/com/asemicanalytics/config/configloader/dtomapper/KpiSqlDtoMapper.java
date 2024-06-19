package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KpiSqlDtoMapper implements Function<KpiSqlDto, Map<String, KpiSqlComponentDto>> {

  @Override
  public Map<String, KpiSqlComponentDto> apply(KpiSqlDto dto) {
    return dto.getAdditionalProperties().entrySet().stream().collect(Collectors.toMap(
        Map.Entry::getKey,
        e -> e.getValue()
    ));
  }
}
