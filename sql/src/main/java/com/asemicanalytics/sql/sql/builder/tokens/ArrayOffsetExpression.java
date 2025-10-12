package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.List;

public class ArrayOffsetExpression implements Expression {

  private final Expression array;
  private final int offset;

  ArrayOffsetExpression(Expression array, int offset) {
    this.array = array;
    this.offset = offset;
  }

  @Override
  public String render(Dialect dialect) {
    return dialect.arrayOffset(array.render(dialect), offset);
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    array.swapTable(oldTable, newTable);
  }
}
