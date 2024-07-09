package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cte implements TableLike {
  private final SelectStatement select;
  private String tag;
  private int index;

  Cte(String tag, int index, SelectStatement select) {
    this.tag = tag;
    this.select = select;
    this.index = index;
  }

  public String name() {
    if (index == 0) {
      return tag;
    }
    return tag + "_" + index;
  }

  public String renderDefinition(Dialect dialect) {
    return dialect.columnIdentifier(name()) + " AS (\n" + select.render(dialect) + ")";
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.tableIdentifier(TableReference.of(name()));
  }

  public SelectStatement select() {
    return select;
  }

  @Override
  public String contentHash() {
    return select.contentHash();
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    select.swapTable(oldTable, newTable);
  }

  public Map<String, Cte> getDependentCtes() {
    return select.getDependentCtes();
  }

  public String tag() {
    return tag;
  }

  public void setIndex(int i) {
    this.index = i;
  }

  @Override
  public String tableName() {
    return tag;
  }

  @Override
  public Optional<Cte> getDependantCte() {
    return Optional.of(this);
  }

  @Override
  public List<String> columnNames() {
    return select.select().columnNames();
  }

  public void merge(Expression... expressions) {
    select.select().merge(new Select(new ExpressionList(expressions)));
  }


}
