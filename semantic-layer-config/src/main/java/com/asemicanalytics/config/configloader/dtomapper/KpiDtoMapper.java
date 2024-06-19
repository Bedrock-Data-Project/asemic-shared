package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.config.configloader.formulaunfolder.KpiFormulaUnfolder;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KpiDtoMapper implements Function<KpiDto, Kpi> {
  private final String appId;
  private final String dateColumn;
  private final List<KpiDto> allKpis;
  private final String parentTable;

  public KpiDtoMapper(String appId, String dateColumn, List<KpiDto> allKpis, String parentTable) {
    this.appId = appId;
    this.dateColumn = dateColumn;
    this.allKpis = allKpis;
    this.parentTable = parentTable;
  }

  @Override
  public Kpi apply(KpiDto dto) {
    Map<String, KpixaxisConfig> xaxisConfig = new HashMap<>();

    var unfolder = new KpiFormulaUnfolder();
    var unfolded = unfolder.evaluate(dto, allKpis);

    xaxisConfig.put(dateColumn, new KpixaxisConfig(
        unfolder.getFormula(unfolded),
        dto.getTotal().map(t -> t.toUpperCase()).orElse("SUM"),
        unfolded.getSql().map(s -> new KpiSqlDtoMapper().apply(s).entrySet().stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                e -> new KpiSqlComponentDtoMapper(appId, parentTable).apply(e.getValue())
            ))).orElse(Map.of()
        )
    ));

    return new Kpi(
        dto.getId(),
        xaxisConfig,
        DefaultLabel.of(dto.getLabel(), dto.getId()),
        Optional.empty(),
        false,
        dto.getDescription(),
        dto.getUnit().map(unitDto -> new Unit(unitDto.getSymbol(), unitDto.getIsPrefix()))
    );
  }
}
