package com.asemicanalytics.sql.sql.builder.tablelike;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import java.util.Map;

public class Cte implements TableLike {
  private final SelectStatement select;
  private String tag;
  private int index;

  public Cte(String tag, int index, SelectStatement select) {
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

  public Map<String, Cte> getDependentCtes() {
    return select.getDependentCtes();
  }

  public String tag() {
    return tag;
  }

  public void setIndex(int i) {
    this.index = i;
  }
}
