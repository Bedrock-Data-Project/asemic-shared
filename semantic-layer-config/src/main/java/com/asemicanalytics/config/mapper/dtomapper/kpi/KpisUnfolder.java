package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.sql.sql.builder.Identifier;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.Formatter;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KpisUnfolder {
  private record KpiIdAndXaxis(String kpiId, String xaxis) {
  }

  private static final String PROPERTY_PREFIX = "property.";
  private static final String KPI_PREFIX = "kpi.";

  private final Map<KpiIdAndXaxis, UnfoldingKpi> kpis = new HashMap<>();
  private final Set<String> propertyIds;

  public KpisUnfolder(List<KpiDto> kpis, Set<String> propertyIds) {
    for (KpiDto kpi : kpis) {
      for (String xaxis : kpi.getxAxis()) {
        var previous = this.kpis.put(
            new KpiIdAndXaxis(kpi.getId(), xaxis),
            new UnfoldingKpi(kpi, xaxis));
        if (previous != null) {
          throw new IllegalArgumentException(
              "Duplicate kpiId and xaxis: " + kpi.getId() + " " + xaxis);
        }
      }
    }
    this.propertyIds = propertyIds;
  }

  public List<UnfoldingKpi> unfold() {
    for (UnfoldingKpi unfoldingKpi : kpis.values()) {
      unfoldKpi(unfoldingKpi);
    }

    return new ArrayList<>(kpis.values());
  }

  private void unfoldKpi(UnfoldingKpi unfoldingKpi) {
    if (unfoldingKpi.isUnfolded()) {
      return;
    }

    Map<String, Token> resolvedComponents = new HashMap<>();

    for (String componentId : Formatter.extractKeys(unfoldingKpi.getFormula())) {
      var token = componentId.split("\\.");
      if (token.length != 2 ||
          (!token[0].equals(PROPERTY_PREFIX) && !token[0].equals(KPI_PREFIX))) {
        throw new IllegalArgumentException("Invalid component id: " + componentId
            + ". Must prefix with property. or kpi.");
      }

      resolveComponent(unfoldingKpi, componentId, token, resolvedComponents);
    }

    unfoldingKpi.unfold(
        Formatter.format(unfoldingKpi.getFormula(), TemplateDict.noMissing(resolvedComponents),
            null));
  }

  private void resolveComponent(UnfoldingKpi unfoldingKpi, String componentId, String[] token,
                                Map<String, Token> resolvedComponents) {

    if (resolvedComponents.containsKey(componentId)) {
      return;
    }

    if (token[0].equals(PROPERTY_PREFIX)) {
      if (!propertyIds.contains(token[1])) {
        throw new IllegalArgumentException("Property not found: " + token[1]);
      }
      resolvedComponents.put(componentId, new Identifier(componentId));
    } else {
      var kpiId = token[1];
      var componentKpi = kpis.get(new KpiIdAndXaxis(kpiId, unfoldingKpi.getXaxis()));
      if (componentKpi == null) {
        throw new IllegalArgumentException(
            "Kpi not found: " + kpiId + " " + unfoldingKpi.getXaxis());
      }

      unfoldKpi(componentKpi);
      resolvedComponents.put(componentId, new Identifier(componentKpi.getFormula()));
      componentKpi.getKpiComponentMap().forEach(unfoldingKpi::addKpiComponent);
    }
  }
}
