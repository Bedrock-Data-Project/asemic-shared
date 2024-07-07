package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.List;
import java.util.StringJoiner;

public class UnionStatement implements Token {

  private final List<SelectStatement> statements;
  private final boolean unionAll;

  public UnionStatement(List<SelectStatement> statements, boolean unionAll) {
    this.statements = statements;
    this.unionAll = unionAll;
  }

  public UnionStatement(List<SelectStatement> statements) {
    this(statements, true);
  }

  @Override
  public String render(Dialect dialect) {
    var unionSeparator = unionAll ? "UNION ALL" : "UNION";
    var stringJoiner = new StringJoiner(unionSeparator + "\n");
    statements.forEach(s -> stringJoiner.add(s.render(dialect)));
    return stringJoiner.toString();
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    statements.forEach(s -> s.swapTable(oldTable, newTable));
  }
}
