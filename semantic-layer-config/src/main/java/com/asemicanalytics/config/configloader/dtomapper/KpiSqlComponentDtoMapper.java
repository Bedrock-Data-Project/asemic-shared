package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import java.util.function.Function;

public class KpiSqlComponentDtoMapper implements Function<KpiSqlComponentDto, KpiComponent> {
  private final String appId;
  private final String parentTable;

  public KpiSqlComponentDtoMapper(String appId, String parentTable) {
    this.appId = appId;
    this.parentTable = parentTable;
  }

  @Override
  public KpiComponent apply(KpiSqlComponentDto dto) {
    return new KpiComponent(
        dto.getSelect(),
        dto.getWhere(),
        TableReference.parse(dto.getFrom().map(from -> from.replace("{app_id}", appId))
            .orElse(parentTable.replace("{app_id}", appId)))
    );
  }
}
