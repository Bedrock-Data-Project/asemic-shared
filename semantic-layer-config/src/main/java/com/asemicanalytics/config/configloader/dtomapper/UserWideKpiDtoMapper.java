package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.configloader.formulaunfolder.UserWideKpiFormulaUnfolder;
import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserWideKpiDtoMapper implements Function<UserWideKpiDto, Kpi> {
  private final String appId;
  private final List<UserWideKpiDto> allKpis;
  private final Optional<String> parentCategory;
  private final String parentTable;

  public UserWideKpiDtoMapper(String appId, List<UserWideKpiDto> allKpis,
                              Optional<String> parentCategory,
                              String parentTable) {
    this.appId = appId;
    this.allKpis = allKpis;
    this.parentCategory = parentCategory;
    this.parentTable = parentTable;
  }

  @Override
  public Kpi apply(UserWideKpiDto dto) {
    Map<String, KpixaxisConfig> xaxisConfig = new HashMap<>();
    for (String xaxisValue : dto.getxAxis()) {
      var kpiDto = new UserWideKpiDto(
          dto.getId(),
          dto.getLabel().orElse(null),
          dto.getDescription().orElse(null),
          dto.getCategory().orElse(null),
          dto.getRecommended().orElse(false),
          dto.getFormula().orElse(null),
          dto.getSql().orElse(null),
          dto.getUnit().orElse(null),
          dto.getxAxis(),
          dto.getTotal().orElse(null)
      );

      var unfolder = new UserWideKpiFormulaUnfolder(xaxisValue);
      var unfolded = unfolder.evaluate(kpiDto, allKpis);

      xaxisConfig.put(xaxisValue, new KpixaxisConfig(
          unfolder.getFormula(unfolded),
          dto.getTotal().map(t -> t.toUpperCase()).orElse("SUM"),
          unfolded.getSql().map(s ->
              s.getAdditionalProperties().entrySet().stream().collect(Collectors.toMap(
                  Map.Entry::getKey,
                  e -> new KpiSqlComponentDtoMapper(
                      appId,
                      Objects.equals(xaxisValue, "cohort_day")
                          ? UserWideDatasource.cohortTable(parentTable)
                          : UserWideDatasource.userActionTable(parentTable, 90)
                  ).apply(e.getValue())
              ))).orElse(Map.of()
          )
      ));
    }

    return new Kpi(
        dto.getId(),
        xaxisConfig,
        DefaultLabel.of(dto.getLabel(), dto.getId()),
        dto.getCategory().or(() -> parentCategory),
        dto.getRecommended().orElse(false),
        dto.getDescription(),
        dto.getUnit().map(unitDto -> new Unit(unitDto.getSymbol(), unitDto.getIsPrefix()))
    );
  }

}
