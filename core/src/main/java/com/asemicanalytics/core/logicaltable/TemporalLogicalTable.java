package com.asemicanalytics.core.logicaltable;

import com.asemicanalytics.core.DataType;
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

public class TemporalLogicalTable<T extends Column> extends LogicalTable<T> {

  public static final String DATE_COLUMN_TAG = "date_column";
  // When an event has no column tagged as the date, the date is derived from the timestamp
  // (DATE(timestamp)) and exposed under this canonical id. See bedrock-query-engine's event-source
  // SQL seam, which renders this id as DATE(<timestamp>) instead of a physical reference.
  public static final String DERIVED_DATE_COLUMN_ID = "asemic_date";
  protected final TimeGrain minTimeGrain;
  protected final Optional<String> dateColumn;
  protected final Map<String, Kpi> kpis;

  public TemporalLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns<T> columns,
                              Map<String, Kpi> kpis, TimeGrain minTimeGrain, Set<String> tags,
                              List<MaterializedIndexTable> materializedIndexTables) {
    super(id, label, description, table, columns, tags, materializedIndexTables);
    this.kpis = new TreeMap<>(kpis);
    this.minTimeGrain = minTimeGrain;
    this.dateColumn = columns.getColumnIdByTagIfExists(DATE_COLUMN_TAG);
  }

  public TemporalLogicalTable(String id, String label, Optional<String> description,
                              TableReference table,
                              Columns<T> columns,
                              Map<String, Kpi> kpis, TimeGrain minTimeGrain, Set<String> tags) {
    this(id, label, description, table, columns, kpis, minTimeGrain, tags, List.of());
  }

  public TimeGrain getMinTimeGrain() {
    return minTimeGrain;
  }

  public Column getDateColumn() {
    if (dateColumn.isPresent()) {
      return columns.column(dateColumn.get());
    }
    return Column.ofHidden(DERIVED_DATE_COLUMN_ID, DataType.DATE).withTag(DATE_COLUMN_TAG);
  }

  public String getDateColumnId() {
    return dateColumn.orElse(DERIVED_DATE_COLUMN_ID);
  }

  /** The id of the column tagged as the date, if one exists. Empty means the date is derived from
   *  the timestamp (DATE(timestamp)) and exposed as {@link #DERIVED_DATE_COLUMN_ID}. */
  public Optional<String> getDateColumnIdIfExists() {
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

