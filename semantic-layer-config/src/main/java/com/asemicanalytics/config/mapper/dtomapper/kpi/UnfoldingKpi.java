package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UnfoldingKpi {
  private final KpiDto kpiDto;
  private final String xaxis;
  private final Set<ComponentId> componentIds;
  private final Map<FilterPath, Formula> formulasByFilter = new HashMap<>();


  public UnfoldingKpi(KpiDto kpiDto, String xaxis, Set<String> propertyIds, Set<String> kpisIds) {
    this.kpiDto = kpiDto;
    this.xaxis = xaxis;
    this.componentIds = Formatter.extractKeys(kpiDto.getSelect()).stream()
        .map(c -> new ComponentId(c, propertyIds, kpisIds))
        .collect(Collectors.toSet());
  }

  public Optional<String> getWhere() {
    return kpiDto.getWhere();
  }

  public String getXaxis() {
    return xaxis;
  }

  public Formula getFormula(FilterPath filterPath) {
    var formula = formulasByFilter.get(filterPath);
    if (formula == null) {
      formula = new Formula(filterPath, kpiDto.getSelect());
      formulasByFilter.put(filterPath, formula);
    }
    return formula;
  }

  public Kpi buildKpi() {
    var formula = getFormula(FilterPath.empty());

    return new Kpi(
        kpiDto.getId(),
        Map.of(
            xaxis,
            new KpixaxisConfig(
                formula.render(),
                kpiDto.getTotalFunction().get().name(),
                formula.getKpiComponentMap())),
        DefaultLabel.of(kpiDto.getLabel(), kpiDto.getId()),
        kpiDto.getCategory(),
        kpiDto.getRecommended().orElse(false),
        kpiDto.getDescription(),
        kpiDto.getUnit().map(unitDto -> new Unit(unitDto.getSymbol(), unitDto.getIsPrefix())),
        kpiDto.getHidden().orElse(false)
    );
  }

  public String getKpiId() {
    return kpiDto.getId();
  }

  public Set<ComponentId> getComponentIds() {
    return componentIds;
  }
}
