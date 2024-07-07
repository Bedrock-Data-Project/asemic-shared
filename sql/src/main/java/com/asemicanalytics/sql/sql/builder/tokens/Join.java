package com.asemicanalytics.sql.sql.builder.tokens;


import com.asemicanalytics.core.Dialect;

public class Join implements Token {

  private final JoinType joinType;
  private TableLike table;
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
    String join = joinType + " JOIN " + table.renderTableDeclaration(dialect);
    if (joinExpression != null) {
      return join + " ON " + joinExpression.render(dialect);
    }
    return join;
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    if (table.equals(oldTable)) {
      table = newTable;

      if (joinExpression != null) {
        joinExpression.swapTable(oldTable, newTable);
      }
    }
  }
}
