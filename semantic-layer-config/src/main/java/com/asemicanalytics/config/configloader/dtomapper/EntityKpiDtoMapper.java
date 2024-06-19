package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.configloader.formulaunfolder.EntityKpiFormulaUnfolder;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityKpiDtoMapper implements Function<EntityKpiDto, Kpi> {
  private final String appId;
  private final List<EntityKpiDto> allKpis;
  private final Optional<String> parentCategory;
  private final String parentTable;

  public EntityKpiDtoMapper(String appId, List<EntityKpiDto> allKpis,
                            Optional<String> parentCategory,
                            String parentTable) {
    this.appId = appId;
    this.allKpis = allKpis;
    this.parentCategory = parentCategory;
    this.parentTable = parentTable;
  }

  @Override
  public Kpi apply(EntityKpiDto dto) {
    Map<String, KpixaxisConfig> xaxisConfig = new HashMap<>();
    for (String xaxisValue : dto.getxAxis()) {
      var kpiDto = new EntityKpiDto(
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

      var unfolder = new EntityKpiFormulaUnfolder(xaxisValue);
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
                          ? EntityLogicalTable.cohortTable(parentTable)
                          : EntityLogicalTable.actionTable(parentTable, 90)
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
