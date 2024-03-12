package com.asemicanalytics.sql.sql.builder.join;


import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;

public class Join implements Token {

  private final JoinType joinType;
  private final TableLike table;
  private BooleanExpression joinExpression;

  public Join(JoinType joinType, TableLike table) {
    this.joinType = joinType;
    this.table = table;
  }

  public static Join inner(TableLike tableLike) {
    return new Join(JoinType.INNER, tableLike);
  }

  public Join on(BooleanExpression joinExpression) {
    this.joinExpression = joinExpression;
    return this;
  }

  public Join and(BooleanExpression joinExpression) {
    this.joinExpression.and(joinExpression);
    return this;
  }

  public Join or(BooleanExpression joinExpression) {
    this.joinExpression.or(joinExpression);
    return this;
  }

  public TableLike table() {
    return table;
  }

  @Override
  public String render(Dialect dialect) {
    return joinType + " JOIN " + table.render(dialect) + " ON " + joinExpression.render(dialect);
  }
}
