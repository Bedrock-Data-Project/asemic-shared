package com.asemicanalytics.core.logicaltable;

import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Columns;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LogicalTable {

  protected final String id;
  protected final String label;
  protected final Optional<String> description;

  protected final TableReference table;
  protected final Columns columns;
  protected final Set<String> tags;
  protected final List<MaterializedIndexTable> materializedIndexTables;

  protected final List<Enrichment> enrichments = new ArrayList<>();

  public LogicalTable(String id, String label, Optional<String> description,
                      TableReference table,
                      Columns columns, Set<String> tags,
                      List<MaterializedIndexTable> materializedIndexTables) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.table = table;
    this.columns = columns;
    this.tags = tags;
    this.materializedIndexTables = materializedIndexTables.stream()
        .sorted((a, b) -> Comparator.comparing(MaterializedIndexTable::cost).compare(a, b))
        .toList();
  }

  public LogicalTable(String id, String label, Optional<String> description,
                      TableReference table,
                      Columns columns, Set<String> tags) {
    this(id, label, description, table, columns, tags, List.of());
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

  public Columns getColumns() {
    return columns;
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
    LogicalTable casted = (LogicalTable) o;
    return id.equals(casted.id);
  }

  public void addEnrichment(Enrichment enrichment) {
    if (enrichments.stream()
        .filter(e -> e.targetLogicalTable().getId().equals(enrichment.targetLogicalTable().getId()))
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Enrichment from logical table already present!");
    }
    enrichments.add(enrichment);
  }

  public Optional<Enrichment> enrichment(LogicalTable targetLogicalTable) {
    return enrichments.stream()
        .filter(e -> e.targetLogicalTable().getId().equals(targetLogicalTable.getId()))
        .findFirst();
  }

  public String getType() {
    return "generic";
  }

  public Set<String> getTags() {
    return tags;
  }

  public boolean hasTag(String tag) {
    return tags.contains(tag);
  }

  public List<MaterializedIndexTable> getMaterializedIndexTables() {
    return materializedIndexTables;
  }

  public Optional<MaterializedIndexTable> getMaterializedIndexTable(String filterExpression) {
    return materializedIndexTables.stream()
        .filter(m -> m.filterExpression().equals(filterExpression))
        .findFirst();
  }
}

