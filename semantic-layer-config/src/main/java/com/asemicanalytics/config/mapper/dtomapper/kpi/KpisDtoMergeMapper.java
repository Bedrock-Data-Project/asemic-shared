package com.asemicanalytics.config.mapper.dtomapper.kpi;

import com.asemicanalytics.config.DefaultLabel;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class KpisDtoMergeMapper
    implements Function<List<EntityKpisDto>, Map<String, KpiDto>> {
  private final List<Integer> cohortDays;

  public KpisDtoMergeMapper(List<Integer> cohortDays) {
    this.cohortDays = cohortDays;
  }

  private String render(String source, String templateValue) {
    return source.replace("{}", templateValue);
  }

  private Map<String, KpiDto> extractKpis(EntityKpisDto dto) {
    Map<String, KpiDto> allKpis = new HashMap<>();

    for (var kpi : dto.getKpis().getAdditionalProperties().entrySet()) {
      List<String> template = kpi.getValue().getTemplate().map(t -> {
        if (t instanceof String) {
          if (t.toString().equals("cohort_day")) {
            return cohortDays.stream().map(Object::toString).toList();
          } else {
            throw new IllegalArgumentException("Invalid template value: " + t);
          }
        }

        if (t instanceof List) {
          return (List<String>) t;
        }

        throw new IllegalArgumentException("Invalid template value: " + t);
      }).orElse(List.of(""));

      template.forEach(t -> {
        String id = render(kpi.getKey(), t);
        allKpis.put(id, new KpiDto(
            render(DefaultLabel.of(kpi.getValue().getLabel(), kpi.getKey()), t),
            kpi.getValue().getDescription().map(v -> render(v, t)).orElse(null),
            kpi.getValue().getSelect(),
            kpi.getValue().getWhere().map(v -> render(v, t)).orElse(null),
            kpi.getValue().getUnit().orElse(null),
            kpi.getValue().getTotalFunction().orElse(KpiDto.TotalFunction.SUM),
            kpi.getValue().getxAxis(),
            null
        ));
      });
    }
    return allKpis;
  }

  @Override
  public Map<String, KpiDto> apply(List<EntityKpisDto> kpisDto) {
    Map<String, KpiDto> allKpisDto = new HashMap<>();
    for (var kpis : kpisDto) {
      for (var entry : extractKpis(kpis).entrySet()) {
        if (allKpisDto.put(entry.getKey(), entry.getValue()) != null) {
          throw new IllegalArgumentException("Duplicate kpi id: " + entry.getKey() + " in entity");
        }
      }
    }
    return allKpisDto;
  }
}
