package com.asemicanalytics.cli.semanticlayer;

import com.asemicanalytics.cli.api.ChartControllerApi;
import com.asemicanalytics.cli.api.DatasourcesControllerApi;
import com.asemicanalytics.cli.invoker.ApiException;
import com.asemicanalytics.cli.model.ChartRequestDto;
import com.asemicanalytics.cli.model.ColumnDto;
import com.asemicanalytics.cli.model.ColumnFilterDto;
import com.asemicanalytics.cli.model.DateIntervalDto;
import com.asemicanalytics.cli.model.KpiDto;
import com.asemicanalytics.cli.semanticlayer.internal.ApiClientFactory;
import com.asemicanalytics.cli.semanticlayer.internal.GlobalConfig;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import picocli.CommandLine;

@CommandLine.Command(name = "validate", mixinStandardHelpOptions = true)
public class ValidateCommand implements Runnable {

  private void testChart(ChartControllerApi api, String logPrefix, KpiDto kpi,
                         boolean isDailyKpi, ColumnDto column) {
    var yesterday = LocalDate.now().minusDays(1);
    try {
      api.submit2(GlobalConfig.getAppId(), new ChartRequestDto()
          .pageId("")
          .requestId("")
          .kpiId(kpi.getId())
          .dateInterval(new DateIntervalDto()
              .dateFrom(yesterday)
              .dateTo(yesterday))
          .xAxis(isDailyKpi ? ChartRequestDto.XAxisEnum.DATE : ChartRequestDto.XAxisEnum.COHORT_DAY)
          .columnFilters(column != null
              ? List.of(new ColumnFilterDto()
              .columnId(column.getId())
              .operation("is_not_null")
              .valueList(List.of()))
              : List.of())
          .columnGroupBys(List.of())
          .timeGrain(ChartRequestDto.TimeGrainEnum.DAY)
          .sortByKpiId(null)
          .groupByLimit(10)
          .dryRun(true));
      System.out.println(
          CommandLine.Help.Ansi.AUTO.string(logPrefix + dots(logPrefix) + "@|fg(green) SUCCESS|@"));
    } catch (ApiException e) {
      System.out.println(
          CommandLine.Help.Ansi.AUTO.string(logPrefix + dots(logPrefix) + "@|fg(red) FAILED|@"));
      if (e.getResponseBody() != null) {
        System.out.println("    " + e.getResponseBody());
      } else {
        System.out.println("    " + e);
      }
    }
  }

  private String dots(String prefix) {
    return ".".repeat(60 - prefix.length());
  }

  @Override
  public void run() {
    String devVersion = "dev/" + UUID.randomUUID();
    System.out.println("pushing temp config...");
    try {
      PushCommand.push(devVersion);
    } catch (IOException | ApiException e) {
      throw new RuntimeException(e);
    }

    var datasourcesApi = new DatasourcesControllerApi(ApiClientFactory.create(
        Map.of("AppConfigVersion", devVersion)));
    var chartApi = new ChartControllerApi(ApiClientFactory.create(
        Map.of("AppConfigVersion", devVersion)));

    try {
      System.out.println("fetching datasources...");
      var datasources = datasourcesApi.daily(GlobalConfig.getAppId());

      for (var datasource : datasources.entrySet()) {
        System.out.println("=====================================");
        System.out.println("validating datasource " + datasource.getKey() + "...");
        System.out.println("=====================================");
        for (var column : datasource.getValue().getColumns()) {
          testChart(chartApi, "column " + column.getId(),
              datasource.getValue().getKpis().get(0),
              datasource.getValue().getKpis().get(0).getIsDailyKpi(), column);
        }
        for (var kpi : datasource.getValue().getKpis()) {
          if (kpi.getIsDailyKpi()) {
            testChart(chartApi, "kpi (daily) " + kpi.getId(), kpi, true, null);
          }
          if (kpi.getIsCohortKpi()) {
            testChart(chartApi, "kpi (cohort) " + kpi.getId(), kpi, false, null);
          }
        }
      }

    } catch (ApiException e) {
      throw new RuntimeException(e);
    }
  }
}
