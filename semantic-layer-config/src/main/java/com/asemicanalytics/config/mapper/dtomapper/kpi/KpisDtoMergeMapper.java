package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiCohortedDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KpisDtoMergeMapper
    implements Function<List<EntityKpisDto>, List<KpiDto>> {
  private final String datecolumn;

  public KpisDtoMergeMapper(String dateColumn) {
    this.datecolumn = dateColumn;
  }

  private String render(String source, int cohortDay) {
    return source.replace("{}", cohortDay + "");
  }

  private List<KpiDto> extractKpis(EntityKpisDto dto) {
    List<KpiDto> allKpis = new ArrayList<>();

    dto.getKpis().orElse(List.of()).forEach(kpi ->
        allKpis.add(new KpiDto(
            kpi.getId(),
            kpi.getLabel().orElse(null),
            kpi.getDescription().orElse(null),
            kpi.getCategory().or(dto::getCategory).orElse(null),
            kpi.getRecommended().orElse(null),
            kpi.getSelect(),
            kpi.getWhere().orElse(null),
            kpi.getUnit().orElse(null),
            kpi.getxAxis(),
            kpi.getTotalFunction().orElse(KpiDto.TotalFunction.SUM),
            kpi.getHidden().orElse(false)
        )));

    dto.getCohortedDailyKpis().orElse(List.of()).forEach(cohortedKpi -> {
      List<Integer> cohortedDays = cohortedKpi.getCohortedDays().orElse(List.of());
      if (cohortedDays.isEmpty()) {
        cohortedDays = dto.getCohortedDailyKpisDays().orElse(List.of());
      }

      cohortedDays.forEach(cohortDay ->
          allKpis.add(new KpiDto(
              render(cohortedKpi.getId(), cohortDay),
              cohortedKpi.getLabel().map(l -> render(l, cohortDay)).orElse(null),
              cohortedKpi.getDescription().orElse(null),
              cohortedKpi.getCategory().or(dto::getCategory).orElse(null),
              cohortedKpi.getRecommeded().orElse(null),
              cohortedKpi.getSelect(),
              cohortedKpi.getWhere().orElse(null),
              cohortedKpi.getUnit().orElse(null),
              List.of(datecolumn),
              KpiDto.TotalFunction.fromValue(
                  cohortedKpi
                      .getTotalFunction()
                      .orElse(KpiCohortedDto.TotalFunction.SUM)
                      .value()),
              cohortedKpi.getHidden().orElse(false)
          )));
    });

    return allKpis;
  }

  @Override
  public List<KpiDto> apply(List<EntityKpisDto> kpisDto) {
    List<KpiDto> allKpisDto = new ArrayList<>();
    kpisDto.forEach(kpiDto -> allKpisDto.addAll(extractKpis(kpiDto)));
    return allKpisDto;
  }
}
