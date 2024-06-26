package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class UnfoldingKpi {
  private final KpiDto kpiDto;
  private String formula;
  private final Map<String, KpiComponent> kpiComponentMap = new TreeMap<>();
  private boolean unfolded = false;
  private final String xaxis;

  public UnfoldingKpi(KpiDto kpiDto, String xaxis) {
    this.kpiDto = kpiDto;
    this.formula = kpiDto.getAggregationExpression();
    this.xaxis = xaxis;
  }

  public String getFormula() {
    return formula;
  }

  public void unfold(String formula) {
    this.formula = formula;
    this.unfolded = true;
  }

  public boolean isUnfolded() {
    return unfolded;
  }

  public String getXaxis() {
    return xaxis;
  }

  public Map<String, KpiComponent> getKpiComponentMap() {
    return Collections.unmodifiableMap(kpiComponentMap);
  }

  public void addKpiComponent(String key, KpiComponent value) {
    if (kpiComponentMap.containsKey(key)) {
      throw new IllegalArgumentException("Kpi component with key " + key + " already exists");
    }
    kpiComponentMap.put(key, value);
  }

  public Kpi buildKpi() {
    return new Kpi(
        kpiDto.getId(),
        Map.of(
            xaxis,
            new KpixaxisConfig(
                getFormula(),
                kpiDto.getTotalFunction().get().name(),
                kpiComponentMap)),
        DefaultLabel.of(kpiDto.getLabel(), kpiDto.getId()),
        kpiDto.getCategory(),
        kpiDto.getRecommended().orElse(false),
        kpiDto.getDescription(),
        kpiDto.getUnit().map(unitDto -> new Unit(unitDto.getSymbol(), unitDto.getIsPrefix()))
    );
  }
}
