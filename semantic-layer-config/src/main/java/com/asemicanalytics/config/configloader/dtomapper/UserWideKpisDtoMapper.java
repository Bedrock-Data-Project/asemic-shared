package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpisDto;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UserWideKpisDtoMapper
    implements Function<List<UserWideKpisDto>, List<UserWideKpiDto>> {
  private final UserWideConfigDto config;
  private final UserWideColumnsDto columnsDto;
  private final String datecolumn;

  public UserWideKpisDtoMapper(
      UserWideConfigDto config, UserWideColumnsDto columnsDto, String dateColumn) {
    this.config = config;
    this.columnsDto = columnsDto;
    this.datecolumn = dateColumn;
  }


  private String render(String source, int cohortDay) {
    return source.replace("{}", cohortDay + "");
  }

  private KpiSqlDto render(KpiSqlDto source, int cohortDay) {
    var sql = new KpiSqlDto();
    source.getAdditionalProperties().entrySet().forEach(e -> {
      sql.getAdditionalProperties().put(
          e.getKey(),
          new KpiSqlComponentDto(
              render((e.getValue()).getSelect(), cohortDay),
              UserWideDatasource.cohortTable(config.getBaseTablePrefix()),
              e.getValue()
                  .getWhere()
                  .map(where -> render(where, cohortDay))
                  .orElse(null))
      );
    });
    return sql;
  }

  private List<UserWideKpiDto> extractKpis(UserWideKpisDto dto) {
    List<UserWideKpiDto> allKpis = new ArrayList<>();
    allKpis.addAll(dto.getKpis().orElse(List.of()));


    dto.getCohortedDailyKpis().orElse(List.of()).forEach(cohortedKpi -> {
      List<Integer> cohortedDays = cohortedKpi.getCohortedDays().orElse(List.of());
      if (cohortedDays.isEmpty()) {
        cohortedDays = dto.getCohortedDailyKpisDays().orElse(List.of());
      }

      cohortedDays.forEach(cohortDay ->
          allKpis.add(new UserWideKpiDto(
              render(cohortedKpi.getId(), cohortDay),
              cohortedKpi.getLabel().map(l -> render(l, cohortDay)).orElse(null),
              cohortedKpi.getDescription().orElse(null),
              cohortedKpi.getCategory().or(() -> dto.getCategory()).orElse(null),
              cohortedKpi.getRecommeded().orElse(null),
              cohortedKpi.getFormula().orElse(null),
              cohortedKpi.getSql().map(sql -> render(sql, cohortDay)).orElse(null),
              cohortedKpi.getUnit().orElse(null),
              List.of(datecolumn),
              cohortedKpi.getTotal().orElse(null)
          )));
    });

    return allKpis;
  }

  @Override
  public List<UserWideKpiDto> apply(List<UserWideKpisDto> kpisDto) {
    List<UserWideKpiDto> allKpisDto = new ArrayList<>();
    for (var kpiDto : kpisDto) {
      extractKpis(kpiDto).forEach(k -> allKpisDto.add(k));
    }

    return allKpisDto;
  }
}
