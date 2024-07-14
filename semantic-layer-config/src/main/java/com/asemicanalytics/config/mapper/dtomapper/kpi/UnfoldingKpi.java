package com.asemicanalytics.config.mapper.dtomapper.kpi;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.identifier;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.core.kpi.Unit;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class UnfoldingKpi {
  private final KpiDto kpiDto;
  private final String xaxis;
  private final Set<ComponentId> componentIds;
  private final Map<FilterPath, Formula> formulasByFilter = new HashMap<>();
  private final Set<String> propertyIds;


  public UnfoldingKpi(KpiDto kpiDto, String xaxis, Set<String> propertyIds, Set<String> kpisIds) {
    this.kpiDto = kpiDto;
    this.xaxis = xaxis;
    this.componentIds = Formatter.extractKeys(kpiDto.getSelect()).stream()
        .map(c -> new ComponentId(c, propertyIds, kpisIds))
        .collect(Collectors.toSet());
    this.propertyIds = propertyIds;
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

  private String unfoldFilter(String filter) {
    // TODO move this somewhere close to CompenentId, to not have this logic duplicated
    return Formatter.format(filter, new TemplateDict(Map.of(), propertyId -> {
      var tokens = propertyId.split("\\.");
      if (tokens.length != 2) {
        throw new IllegalArgumentException("Invalid property id: " + propertyId);
      }
      if (!tokens[0].equals(ComponentId.PROPERTY_PREFIX)) {
        throw new IllegalArgumentException("Invalid property id: " + propertyId);
      }
      if (!propertyIds.contains(tokens[1])) {
        throw new IllegalArgumentException("Property not found: " + tokens[1]);
      }
      return identifier("{" + tokens[1] + "}");
    }), null);
  }

  private Map<String, KpiComponent> unfoldFilters(Map<String, KpiComponent> components) {
    Map<String, KpiComponent> result = new HashMap<>();
    for (var entry : components.entrySet()) {
      var componentId = entry.getKey();
      var component = entry.getValue();
      var filters = component.filters().stream()
          .map(this::unfoldFilter)
          .collect(Collectors.toSet());
      result.put(componentId, new KpiComponent(component.select(), new TreeSet<>(filters)));
    }
    return result;
  }

  public Kpi buildKpi() {
    var formula = getFormula(FilterPath.empty());

    Map<String, KpixaxisConfig> xaxisConfig = new HashMap<>();
    xaxisConfig.put(
        xaxis,
        new KpixaxisConfig(
            formula.render(),
            kpiDto.getTotalFunction().get().name(),
            unfoldFilters(formula.getKpiComponentMap())));
    return new Kpi(
        kpiDto.getId(),
        xaxisConfig,
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
