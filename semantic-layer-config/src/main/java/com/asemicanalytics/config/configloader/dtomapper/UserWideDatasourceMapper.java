package com.asemicanalytics.config.configloader.dtomapper;

import com.asemicanalytics.config.configparser.UserWideDatasourceDto;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.datasource.useraction.ActivityUserActionDatasource;
import com.asemicanalytics.core.datasource.useraction.RegistrationUserActionDatasource;
import com.asemicanalytics.core.datasource.userwide.RegistrationColumn;
import com.asemicanalytics.core.datasource.userwide.TotalColumn;
import com.asemicanalytics.core.datasource.userwide.UserActionColumn;
import com.asemicanalytics.core.datasource.userwide.UserWideDatasource;
import com.asemicanalytics.core.kpi.Kpi;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideColumnsDto;
import com.asemicanalytics.semanticlayer.config.dto.v1.semantic_layer.UserWideKpiDto;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserWideDatasourceMapper
    implements Function<UserWideDatasourceDto, UserWideDatasource> {
  private final String appId;

  public UserWideDatasourceMapper(String appId) {
    this.appId = appId;
  }

  @Override
  public UserWideDatasource apply(UserWideDatasourceDto dto) {
    var registrationDatasource = (RegistrationUserActionDatasource) dto.userActionDatasources()
        .values().stream()
        .filter(d -> d instanceof RegistrationUserActionDatasource)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No user datasource tagged with "
            + RegistrationUserActionDatasource.DATASOURCE_TAG + " user action datasource found"));

    UserWideColumnsDto mergedColumns = new UserWideColumnsDtoMapper().apply(dto.columns());
    List<UserWideKpiDto> mergedKpis = new UserWideKpisDtoMapper(
        dto.config(), mergedColumns, registrationDatasource.getDateColumnId()).apply(dto.kpis());

    var registrationColumnsMap =
        mergedColumns.getRegistrationColumns().stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            c -> new RegistrationColumn(new ColumnDtoMapper().apply(c.getColumn()),
                c.getSourceColumn().orElse(c.getColumn().getId())), (a, b) -> a,
            LinkedHashMap::new));

    var userActionsColumnsMap =
        mergedColumns.getUserActionColumns().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(), c -> {
              var dataSource = dto.userActionDatasources().get(c.getUserActionDatasource());
              if (dataSource == null) {
                throw new IllegalArgumentException("User action datasource not found: "
                    + c.getUserActionDatasource()
                    + " in user wide for column: "
                    + c.getColumn().getId());
              }

              var window = c.getWindow().orElse(List.of(0, 0));
              if (window.isEmpty()) {
                window.add(0);
                window.add(0);
              }

              if (window.size() != 2) {
                throw new IllegalArgumentException("window must have 2 values"
                    + " in user wide for column: "
                    + c.getColumn().getId());
              }
              if (window.get(0) > 0 || window.get(1) > 0) {
                throw new IllegalArgumentException("window days must 0 or negative"
                    + " in user wide for column: "
                    + c.getColumn().getId());
              }
              var windowAggregation = c.getWindowAggregation().orElse("MAX");
              if (!Set.of("MAX", "MIN", "SUM", "AVG").contains(windowAggregation.toUpperCase())) {
                throw new IllegalArgumentException(
                    "Invalid window aggregation: " + windowAggregation
                        + " in user wide for column: "
                        + c.getColumn().getId());
              }

              return new UserActionColumn(new ColumnDtoMapper().apply(c.getColumn()),
                  dataSource,
                  c.getWhere(), c.getAggregationExpression(),
                  Optional.of(LocalDate.MIN), c.getMissingValue(),
                  window.get(0), window.get(1), windowAggregation,
                  c.getCanMaterialize().orElse(true));
            }, (a, b) -> a, LinkedHashMap::new));

    var totalColumnsMap =
        mergedColumns.getTotalColumns().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(),
            c -> new TotalColumn(
                new ColumnDtoMapper().apply(c.getColumn()),
                c.getSourceColumn(),
                c.getMergeExpression()), (a, b) -> a, LinkedHashMap::new));

    var computedColumnsMap =
        mergedColumns.getComputedColumns().orElse(List.of()).stream().collect(Collectors.toMap(
            c -> c.getColumn().getId(), new ColumnComputedDtoMapper(), (a, b) -> a,
            LinkedHashMap::new
        ));

    SequencedMap<String, Column> columns = new LinkedHashMap<>();
    registrationColumnsMap.values().forEach(c -> columns.put(c.getId(), c));
    userActionsColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in user wide");
      }
    });
    totalColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in user wide");
      }
    });
    computedColumnsMap.values().forEach(c -> {
      if (columns.put(c.getId(), c) != null) {
        throw new IllegalArgumentException("Duplicate column id: " + c.getId() + " in user wide");
      }
    });

    var kpis = buildKpisMap(dto, mergedKpis);

    var activityDatasource = (ActivityUserActionDatasource) dto.userActionDatasources()
        .values().stream()
        .filter(d -> d instanceof ActivityUserActionDatasource)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No user datasource tagged with "
            + ActivityUserActionDatasource.DATASOURCE_TAG + " user action datasource found"));

    return new UserWideDatasource(
        dto.config().getBaseTablePrefix().replace("{app_id}", appId),
        !columns.isEmpty() ? Optional.of(new Columns(columns)) : Optional.empty(),
        registrationDatasource,
        activityDatasource,

        // TODO should be able to configure this
        List.of(1, 90),
        List.of(1, 2, 3, 4, 5, 6, 7, 14, 30, 60, 90, 180, 360),

        kpis);
  }

  private Map<String, Kpi> buildKpisMap(UserWideDatasourceDto dto,
                                        List<UserWideKpiDto> mergedKpis) {
    Map<String, Kpi> kpis = new HashMap<>();
    for (var kpiDto : mergedKpis) {
      var kpi = new UserWideKpiDtoMapper(
          appId, mergedKpis, Optional.empty(), dto.config().getBaseTablePrefix())
          .apply(kpiDto);
      if (!kpis.containsKey(kpi.id())) {
        kpis.put(kpi.id(), kpi);
      } else {
        kpis.get(kpi.id()).merge(kpi);
      }
    }
    return kpis;
  }
}
