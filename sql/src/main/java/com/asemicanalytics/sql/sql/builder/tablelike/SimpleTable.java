package com.asemicanalytics.sql.sql.builder.tablelike;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import java.util.Optional;

public class SimpleTable implements TableLike {
  private final TableReference tableReference;

  public SimpleTable(TableReference tableReference) {
    this.tableReference = tableReference;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.tableIdentifier(tableReference);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {

  }

  @Override
  public Optional<Cte> getDependantCte() {
    return Optional.empty();
  }


}
