package com.asemicanalytics.config.mapper.dtomapper.kpi;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.identifier;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KpisUnfolder {
  private record KpiIdAndXaxis(String kpiId, String xaxis) {
  }

  private final Map<KpiIdAndXaxis, UnfoldingKpi> kpis = new HashMap<>();
  private final PropertyIdRewritter propertyIdRewritter = new PropertyIdRewritter();


  public KpisUnfolder(Map<String, KpiDto> kpis, Set<String> propertyIds) {
    for (var kpiEntry : kpis.entrySet()) {
      var kpi = kpiEntry.getValue();
      for (var xaxisEntry : kpi.getxAxis().getAdditionalProperties().entrySet()) {
        var previous = this.kpis.put(
            new KpiIdAndXaxis(kpiEntry.getKey(), xaxisEntry.getKey()),
            new UnfoldingKpi(kpiEntry.getKey(), kpi,
                xaxisEntry.getKey(), xaxisEntry.getValue(),
                propertyIds, kpis.keySet()));
        if (previous != null) {
          throw new IllegalArgumentException(
              "Duplicate kpiId and xaxis: " + kpiEntry.getKey() + " " + xaxisEntry.getKey());
        }
      }
    }
  }

  public List<UnfoldingKpi> unfold() {
    propertyIdRewritter.reset();
    for (UnfoldingKpi unfoldingKpi : kpis.values()) {
      unfoldKpi(unfoldingKpi, FilterPath.empty(), Set.of());
    }

    return new ArrayList<>(kpis.values());
  }

  private void unfoldKpi(UnfoldingKpi unfoldingKpi, FilterPath filterPath, Set<String> parents) {

    var formula = unfoldingKpi.getFormula(filterPath);
    if (formula.rendered()) {
      return;
    }

    FilterPath filtersIncludingThis = unfoldingKpi.getWhere()
        .map(filterPath::plus)
        .orElse(filterPath);

    for (ComponentId componentId : unfoldingKpi.getComponentIds()) {
      var kpiComponent = componentId.buildKpiComponent(filtersIncludingThis);

      if (componentId.isProperty()) {
        String rewrittenId = propertyIdRewritter.rewrite(componentId, kpiComponent);
        formula.addResolvedComponent(componentId.toString(),
            identifier("{" + rewrittenId + "}"));
        formula.addKpiComponent(rewrittenId, kpiComponent);
      }

      if (componentId.isKpi()) {
        var kpiAndAxis = new KpiIdAndXaxis(componentId.getId(), unfoldingKpi.getXaxis());
        var kpi = kpis.get(kpiAndAxis);
        if (kpi == null) {
          throw new IllegalArgumentException(
              "Kpi not found: " + componentId.getId() + " " + unfoldingKpi.getXaxis());
        }
        if (parents.contains(kpi.getKpiId())) {
          throw new IllegalArgumentException(
              "Cycle detected: " + parents + " " + unfoldingKpi.getKpiId());
        }

        var newParents = new HashSet<>(parents);
        newParents.add(unfoldingKpi.getKpiId());
        unfoldKpi(kpi, filtersIncludingThis, newParents);
        var kpiFormula = kpi.getFormula(filtersIncludingThis);
        kpiFormula.getKpiComponentMap().forEach(formula::addKpiComponent);
        formula.addResolvedComponent(componentId.toString(),
            identifier("(" + kpiFormula.render() + ")"));
      }
    }
    formula.render();
  }
}
