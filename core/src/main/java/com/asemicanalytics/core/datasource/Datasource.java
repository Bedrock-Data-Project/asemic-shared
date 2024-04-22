package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.ComputedColumn;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.SequencedMap;

public class Datasource {

  protected final String id;
  protected final String label;
  protected final Optional<String> description;

  protected final TableReference table;
  protected final SequencedMap<String, Column> tableColumns;
  protected final SequencedMap<String, ComputedColumn> computedColumns;

  protected final List<Enrichment> enrichments = new ArrayList<>();

  public Datasource(String id, String label, Optional<String> description,
                    TableReference table,
                    SequencedMap<String, Column> columns,
                    SequencedMap<String, ComputedColumn> computedColumns) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.table = table;
    this.tableColumns = columns;
    this.computedColumns = computedColumns;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public Optional<String> getDescription() {
    return description;
  }

  public TableReference getTable() {
    return table;
  }

  public SequencedMap<String, Column> getTableColumns() {
    return tableColumns;
  }

  public SequencedMap<String, ComputedColumn> getComputedColumns() {
    return computedColumns;
  }

  public SequencedMap<String, Column> getAllColumns() {
    var allColumns = new LinkedHashMap<String, Column>();
    allColumns.putAll(tableColumns);
    allColumns.putAll(computedColumns);
    return allColumns;
  }

  public Column column(String id) {
    return getAllColumns().get(id);
  }

  public List<Enrichment> getEnrichments() {
    return enrichments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Datasource casted = (Datasource) o;
    return id.equals(casted.id);
  }

  public void addEnrichment(Enrichment enrichment) {
    if (enrichments.stream()
        .filter(e -> e.targetDatasource().getId().equals(enrichment.targetDatasource().getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Enrichment from datasource already present!");
    }
    enrichments.add(enrichment);
  }

  public Optional<Enrichment> enrichment(Datasource targetDatasource) {
    return enrichments.stream()
        .filter(e -> e.targetDatasource().getId().equals(targetDatasource.getId()))
        .findFirst();
  }

  public String getType() {
    return "generic";
  }
}

