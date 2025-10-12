package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArrayAggExpression implements Expression {

  private final Expression aggExpression;
  private ExpressionList orderBy = new ExpressionList();
  private boolean desc;
  private Integer limit;

  ArrayAggExpression(Expression aggExpression) {
    this.aggExpression = aggExpression;
  }

  public ArrayAggExpression orderBy(Expression... orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    return this;
  }

  public ArrayAggExpression orderBy(List<Expression> orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    return this;
  }

  public ArrayAggExpression orderByDesc(Expression... orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    this.desc = true;
    return this;
  }

  public ArrayAggExpression orderByDesc(List<Expression> orderBy) {
    this.orderBy = ExpressionList.inline(orderBy);
    this.desc = true;
    return this;
  }

  public ArrayAggExpression limit(int limit) {
    this.limit = limit;
    return this;
  }

  public Expression atOffset(int offset) {
    return new ArrayOffsetExpression(this, offset);
  }

  @Override
  public String render(Dialect dialect) {
    List<String> tokens = new ArrayList<>();
    if (!orderBy.isEmpty()) {
      tokens.add("ORDER BY " + this.orderBy.render(dialect) + (desc ? " DESC" : ""));
    }

    if (!tokens.isEmpty()) {
      tokens.addFirst(" ");
    }
    return "ARRAY_AGG(" + aggExpression.render(dialect) + String.join(" ", tokens) + ")";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    aggExpression.swapTable(oldTable, newTable);
    orderBy.swapTable(oldTable, newTable);
  }
}
