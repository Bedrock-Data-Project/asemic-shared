package com.asemicanalytics.config.configloader.formulaunfolder;

import com.asemicanalytics.config.configloader.dtomapper.KpiSqlDtoMapper;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KpiFormulaUnfolder extends FormulaUnfolder<KpiDto> {

  @Override
  public String getFormula(KpiDto kpiDto) {
    return getFormula(kpiDto.getId(), kpiDto.getFormula(), kpiDto.getSql());
  }

  @Override
  protected Map<String, KpiSqlComponentDto> getComponents(KpiDto kpiDto) {
    return new HashMap<String, KpiSqlComponentDto>(kpiDto.getSql()
        .map(new KpiSqlDtoMapper())
        .orElse(Map.of()));
  }

  @Override
  protected KpiDto cloneKpi(KpiDto kpiDto, String formula, KpiSqlDto kpiSqlDto) {
    return new KpiDto(
        kpiDto.getId(),
        kpiDto.getLabel().orElse(null),
        kpiDto.getDescription().orElse(null),
        formula,
        kpiSqlDto,
        kpiDto.getUnit().orElse(null),
        kpiDto.getTotal().orElse(null)
    );
  }

  @Override
  protected Optional<KpiDto> findKpiDto(String id, List<KpiDto> allKpis) {
    return allKpis.stream().filter(kpi -> kpi.getId().equals(id)).findFirst();
  }
}
