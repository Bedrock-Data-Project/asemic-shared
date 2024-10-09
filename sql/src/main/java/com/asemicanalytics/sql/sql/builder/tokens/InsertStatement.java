package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

public class InsertStatement implements StandaloneStatement {
  private TableLike table;
  private SelectStatement selectStatement;


  InsertStatement() {
  }

  public InsertStatement into(TableLike table) {
    this.table = table;
    return this;
  }

  public InsertStatement select(SelectStatement selectStatement) {
    this.selectStatement = selectStatement;
    return this;
  }

  @Override
  public String renderBeforeCte(Dialect dialect) {
    return "INSERT INTO " + table.render(dialect) + "\n";
  }

  @Override
  public String renderAfterCte(Dialect dialect) {
    return selectStatement.render(dialect);
  }


  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    if (table.equals(oldTable)) {
      table = newTable;
    }
    selectStatement.swapTable(oldTable, newTable);

  }
}
