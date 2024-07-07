package com.asemicanalytics.sql.sql.builder.select;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.expression.AliasedExpression;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.ExpressionList;
import com.asemicanalytics.sql.sql.builder.expression.TableColumn;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.List;

public class Select implements Token {
  private final ExpressionList expressions;

  public Select(ExpressionList expressions) {
    this.expressions = expressions;
  }

  @Override
  public String render(Dialect dialect) {
    return "SELECT\n  " + expressions.renderDefinition(dialect) + "\n";
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expressions.swapTable(oldTable, newTable);
  }

  public void addExpression(Expression expression) {
    expressions.add(expression);
  }

  public void popExpression() {
    expressions.pop();
  }

  public void merge(Select select) {
    expressions.merge(select.expressions);
  }

  public List<Expression> expressions() {
    return expressions.expressions();
  }

  public List<String> columnNames() {
    return expressions.expressions().stream().map(e -> {
      if (e instanceof AliasedExpression) {
        return ((AliasedExpression) e).alias();
      }
      if (e instanceof TableColumn) {
        return ((TableColumn) e).name();
      }

      throw new IllegalArgumentException("Unsupported expression type: " + e.getClass().getName());
    }).toList();
  }
}
