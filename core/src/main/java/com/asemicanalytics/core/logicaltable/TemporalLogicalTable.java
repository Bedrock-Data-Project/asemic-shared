package com.asemicanalytics.core.logicaltable;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrain;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.kpi.Kpi;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public class TemporalLogicalTable extends LogicalTable {

  public static final String DATE_COLUMN_TAG = "date_column";
  protected final TimeGrain minTimeGrain;
  protected final String dateColumn;
  protected final Map<String, Kpi> kpis;

  public TemporalLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns columns,
                              Map<String, Kpi> kpis, TimeGrain minTimeGrain, Set<String> tags,
                              List<MaterializedIndexTable> materializedIndexTables) {
    super(id, label, description, table, columns, tags, materializedIndexTables);
    this.kpis = new TreeMap<>(kpis);
    this.minTimeGrain = minTimeGrain;
    this.dateColumn = columns.getColumnIdByTag(DATE_COLUMN_TAG);
  }

  public TemporalLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns columns,
                              Map<String, Kpi> kpis, TimeGrain minTimeGrain, Set<String> tags) {
    this(id, label, description, table, columns, kpis, minTimeGrain, tags, List.of());
  }

  public TimeGrain getMinTimeGrain() {
    return minTimeGrain;
  }

  public Column getDateColumn() {
    return columns.column(dateColumn);
  }

  public String getDateColumnId() {
    return dateColumn;
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

