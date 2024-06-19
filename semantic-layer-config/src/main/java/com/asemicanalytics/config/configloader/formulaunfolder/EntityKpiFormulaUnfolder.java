package com.asemicanalytics.config.configloader.formulaunfolder;

import com.asemicanalytics.config.configloader.dtomapper.KpiSqlDtoMapper;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityKpiFormulaUnfolder extends FormulaUnfolder<EntityKpiDto> {

  private final String xaxis;

  public EntityKpiFormulaUnfolder(String xaxis) {
    this.xaxis = xaxis;
  }

  @Override
  public String getFormula(EntityKpiDto kpiDto) {
    return getFormula(kpiDto.getId(), kpiDto.getFormula(), kpiDto.getSql());
  }

  @Override
  protected Map<String, KpiSqlComponentDto> getComponents(EntityKpiDto kpiDto) {
    return new HashMap<>(kpiDto.getSql()
        .map(new KpiSqlDtoMapper())
        .orElse(Map.of()));
  }

  @Override
  protected EntityKpiDto cloneKpi(EntityKpiDto kpiDto, String formula, KpiSqlDto kpiSqlDto) {
    return new EntityKpiDto(
        kpiDto.getId(),
        kpiDto.getLabel().orElse(null),
        kpiDto.getDescription().orElse(null),
        kpiDto.getCategory().orElse(null),
        kpiDto.getRecommended().orElse(null),
        formula,
        kpiSqlDto,
        kpiDto.getUnit().orElse(null),
        kpiDto.getxAxis(),
        kpiDto.getTotal().orElse(null)
    );
  }

  @Override
  protected Optional<EntityKpiDto> findKpiDto(String id, List<EntityKpiDto> allKpis) {
    return allKpis.stream()
        .filter(kpi -> kpi.getId().equals(id) && kpi.getxAxis().contains(xaxis))
        .findFirst();
  }
}
