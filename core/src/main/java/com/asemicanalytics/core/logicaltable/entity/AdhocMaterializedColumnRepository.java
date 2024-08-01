package com.asemicanalytics.core.logicaltable.entity;

import com.asemicanalytics.core.column.Columns;
import java.time.LocalDate;
import java.util.Optional;

public class AdhocMaterializedColumnRepository implements MaterializedColumnRepository {
  private final MaterializedColumnRepository repository;
  private final Columns adhocColumns;

  public AdhocMaterializedColumnRepository(MaterializedColumnRepository repository,
                                           Columns adhocColumns) {
    this.repository = repository;
    this.adhocColumns = adhocColumns;
  }


  @Override
  public Optional<LocalDate> materializedFrom(String columnId) {
    if (adhocColumns.hasColumn(columnId)) {
      return Optional.empty();
    }
    return repository.materializedFrom(columnId);
  }
}
