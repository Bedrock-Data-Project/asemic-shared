package com.asemicanalytics.config.configloader.formulaunfolder;

import com.asemicanalytics.config.configloader.dtomapper.KpiSqlDtoMapper;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserWideKpiFormulaUnfolder extends FormulaUnfolder<UserWideKpiDto> {

  private final String xaxis;

  public UserWideKpiFormulaUnfolder(String xaxis) {
    this.xaxis = xaxis;
  }

  @Override
  public String getFormula(UserWideKpiDto kpiDto) {
    return getFormula(kpiDto.getId(), kpiDto.getFormula(), kpiDto.getSql());
  }

  @Override
  protected Map<String, KpiSqlComponentDto> getComponents(UserWideKpiDto kpiDto) {
    return new HashMap<>(kpiDto.getSql()
        .map(new KpiSqlDtoMapper())
        .orElse(Map.of()));
  }

  @Override
  protected UserWideKpiDto cloneKpi(UserWideKpiDto kpiDto, String formula, KpiSqlDto kpiSqlDto) {
    return new UserWideKpiDto(
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
  protected Optional<UserWideKpiDto> findKpiDto(String id, List<UserWideKpiDto> allKpis) {
    return allKpis.stream()
        .filter(kpi -> kpi.getId().equals(id) && kpi.getxAxis().contains(xaxis))
        .findFirst();
  }
}
