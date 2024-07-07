package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.sql.sql.builder.tokens.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KpisUnfolder {
  private record KpiIdAndXaxis(String kpiId, String xaxis) {
  }

  private final Map<KpiIdAndXaxis, UnfoldingKpi> kpis = new HashMap<>();
  private final Set<String> propertyIds;
  private final PropertyIdRewritter propertyIdRewritter = new PropertyIdRewritter();


  public KpisUnfolder(List<KpiDto> kpis, Set<String> propertyIds) {
    Set<String> kpiIds = kpis.stream().map(KpiDto::getId).collect(Collectors.toSet());
    for (KpiDto kpi : kpis) {
      for (String xaxis : kpi.getxAxis()) {
        var previous = this.kpis.put(
            new KpiIdAndXaxis(kpi.getId(), xaxis),
            new UnfoldingKpi(kpi, xaxis, propertyIds, kpiIds));
        if (previous != null) {
          throw new IllegalArgumentException(
              "Duplicate kpiId and xaxis: " + kpi.getId() + " " + xaxis);
        }
      }
    }
    this.propertyIds = propertyIds;
  }

  public List<UnfoldingKpi> unfold() {
    propertyIdRewritter.reset();

    // TODO PARTITION BY COMPONENTS AND CHECK CYCLES
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
            new Identifier("{" + rewrittenId + "}"));
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
        unfoldKpi(kpis.get(kpiAndAxis), filtersIncludingThis, newParents);
        var kpiFormula = kpis.get(kpiAndAxis).getFormula(filtersIncludingThis);
        kpiFormula.getKpiComponentMap().forEach(formula::addKpiComponent);
        formula.addResolvedComponent(componentId.toString(),
            new Identifier("(" + kpiFormula.render() + ")"));
      }
    }
  }
}
