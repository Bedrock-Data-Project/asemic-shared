package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrain;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.ComputedColumn;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;

public class TemporalDatasource extends Datasource {

  protected final TimeGrain minTimeGrain;
  protected final String dateColumn;
  protected final Map<String, Kpi> kpis;

  public TemporalDatasource(String id, String label, Optional<String> description,
                            TableReference table,
                            SequencedMap<String, Column> columns,
                            SequencedMap<String, ComputedColumn> computedColumns,
                            Map<String, Kpi> kpis, TimeGrain minTimeGrain, String dateColumn) {
    super(id, label, description, table, columns, computedColumns);
    this.minTimeGrain = minTimeGrain;
    this.dateColumn = dateColumn;
    this.kpis = kpis;
  }

  public TimeGrain getMinTimeGrain() {
    return minTimeGrain;
  }

  public Column getDateColumn() {
    return column(dateColumn);
  }

  public Map<String, Kpi> getKpis() {
    return kpis;
  }

  public Kpi kpi(String id) {
    return kpis.get(id);
  }

  public String getType() {
    return "temporal";
  }
}

