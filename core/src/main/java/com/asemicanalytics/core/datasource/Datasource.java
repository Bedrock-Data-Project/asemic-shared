package com.asemicanalytics.core.datasource;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SequencedMap;

public class Datasource {

  protected final String id;
  protected final String label;
  protected final Optional<String> description;

  protected final TableReference table;
  protected final SequencedMap<String, Column> columns;

  protected final List<Enrichment> enrichments = new ArrayList<>();

  public Datasource(String id, String label, Optional<String> description,
                    TableReference table,
                    SequencedMap<String, Column> columns) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.table = table;
    this.columns = columns;

    if (columns.isEmpty()) {
      throw new IllegalArgumentException("Datasource " + id + " must have at least one column");
    }
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

  public SequencedMap<String, Column> getColumns() {
    return columns;
  }

  public Column column(String id) {
    return getColumns().get(id);
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

