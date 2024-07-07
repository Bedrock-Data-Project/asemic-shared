package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.ContentHashDialect;
import java.util.List;
import java.util.Optional;

public interface TableLike extends Token {
  default Expression column(String columnName) {
    return new TableColumn(this, columnName);
  }

  default String renderTableDeclaration(Dialect dialect) {
    return render(dialect);
  }

  /**
   * Used to resolve rendering order of CTEs.
   */
  Optional<Cte> getDependantCte();

  default boolean equals(TableLike other) {
    return this.render(new ContentHashDialect()).equals(other.render(new ContentHashDialect()));
  }

  List<String> columnNames();
}
