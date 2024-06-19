package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.core.logicaltable.entity.EntityLogicalTable;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityConfigDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpiDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityKpisDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.EntityPropertiesDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlComponentDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.KpiSqlDto;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EntityKpisDtoMapper
    implements Function<List<EntityKpisDto>, List<EntityKpiDto>> {
  private final EntityConfigDto config;
  private final EntityPropertiesDto columnsDto;
  private final String datecolumn;

  public EntityKpisDtoMapper(
      EntityConfigDto config, EntityPropertiesDto columnsDto, String dateColumn) {
    this.config = config;
    this.columnsDto = columnsDto;
    this.datecolumn = dateColumn;
  }


  private String render(String source, int cohortDay) {
    return source.replace("{}", cohortDay + "");
  }

  private KpiSqlDto render(KpiSqlDto source, int cohortDay) {
    var sql = new KpiSqlDto();
    source.getAdditionalProperties().entrySet().forEach(e -> {
      sql.getAdditionalProperties().put(
          e.getKey(),
          new KpiSqlComponentDto(
              render((e.getValue()).getSelect(), cohortDay),
              EntityLogicalTable.cohortTable(config.getBaseTablePrefix()),
              e.getValue()
                  .getWhere()
                  .map(where -> render(where, cohortDay))
                  .orElse(null))
      );
    });
    return sql;
  }

  private List<EntityKpiDto> extractKpis(EntityKpisDto dto) {
    List<EntityKpiDto> allKpis = new ArrayList<>();
    allKpis.addAll(dto.getKpis().orElse(List.of()));


    dto.getCohortedDailyKpis().orElse(List.of()).forEach(cohortedKpi -> {
      List<Integer> cohortedDays = cohortedKpi.getCohortedDays().orElse(List.of());
      if (cohortedDays.isEmpty()) {
        cohortedDays = dto.getCohortedDailyKpisDays().orElse(List.of());
      }

      cohortedDays.forEach(cohortDay ->
          allKpis.add(new EntityKpiDto(
              render(cohortedKpi.getId(), cohortDay),
              cohortedKpi.getLabel().map(l -> render(l, cohortDay)).orElse(null),
              cohortedKpi.getDescription().orElse(null),
              cohortedKpi.getCategory().or(() -> dto.getCategory()).orElse(null),
              cohortedKpi.getRecommeded().orElse(null),
              cohortedKpi.getFormula().orElse(null),
              cohortedKpi.getSql().map(sql -> render(sql, cohortDay)).orElse(null),
              cohortedKpi.getUnit().orElse(null),
              List.of(datecolumn),
              cohortedKpi.getTotal().orElse(null)
          )));
    });

    return allKpis;
  }

  @Override
  public List<EntityKpiDto> apply(List<EntityKpisDto> kpisDto) {
    List<EntityKpiDto> allKpisDto = new ArrayList<>();
    for (var kpiDto : kpisDto) {
      extractKpis(kpiDto).forEach(k -> allKpisDto.add(k));
    }

    return allKpisDto;
  }
}
