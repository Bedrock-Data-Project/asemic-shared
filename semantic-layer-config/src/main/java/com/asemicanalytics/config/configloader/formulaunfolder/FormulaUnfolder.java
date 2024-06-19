package com.asemicanalytics.config.configloader.formulaunfolder;

import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import com.asemicanalytics.sql.sql.builder.Identifier;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.Formatter;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class FormulaUnfolder<K> {

  public String getFormula(String kpiId, Optional<String> formula, Optional<KpiSqlDto> sql) {
    return formula.orElseGet(() -> {
      if (sql.isEmpty()
          || sql.get().getAdditionalProperties().size() != 1
          || !sql.get().getAdditionalProperties().containsKey(kpiId)) {
        throw new IllegalArgumentException(
            "When formula is not defined, there should be only one sql component named as kpi id");
      }

      return "{" + kpiId + "}";
    });
  }

  protected abstract String getFormula(K kpiDto);

  protected abstract Map<String, KpiSqlComponentDto> getComponents(K kpiDto);

  protected abstract K cloneKpi(K kpiDto, String formula, KpiSqlDto kpiSqlDto);

  protected abstract Optional<K> findKpiDto(String id, List<K> allKpis);

  public K evaluate(K kpiDto, List<K> allKpis) {

    var resolvedComponents = getComponents(kpiDto);
    Map<String, Token> resolvedComponentsId = new HashMap<>();
    var formula =
        unfoldFormula(getFormula(kpiDto), resolvedComponents, resolvedComponentsId, allKpis);

    var sqlDto = new KpiSqlDto();
    resolvedComponents.forEach((k, v) -> sqlDto.setAdditionalProperty(k, v));
    return cloneKpi(kpiDto, formula, sqlDto);
  }

  private String unfoldFormula(String formula,
                               Map<String, KpiSqlComponentDto> resolvedComponents,
                               Map<String, Token> resolvedComponentsId,
                               List<K> allKpis) {
    Set<String> keys = Formatter.extractKeys(formula);

    for (String key : Formatter.extractKeys(formula)) {
      if (resolvedComponentsId.containsKey(key)) {
        continue;
      }

      if (resolvedComponents.containsKey(key)) {
        resolvedComponentsId.put(key, new Identifier("{" + key + "}"));
        continue;
      }

      K referencedKpi = findKpiDto(key, allKpis)
          .orElseThrow(() -> new IllegalArgumentException("Cannot find kpi " + key));

      getComponents(referencedKpi).forEach((id, component) -> {
        if (resolvedComponents.containsKey(id)
            && !resolvedComponents.get(id).equals(component)) {
          // TODO we can try to change the name of the component to avoid conflict
          throw new IllegalArgumentException("Conflict with " + id);
        }
        resolvedComponents.put(id, component);
      });
      resolvedComponentsId.put(key,
          new Identifier(unfoldFormula(getFormula(referencedKpi), resolvedComponents,
              resolvedComponentsId, allKpis)));
    }

    return Formatter.format(formula, TemplateDict.noMissing(resolvedComponentsId), null);
  }
}
