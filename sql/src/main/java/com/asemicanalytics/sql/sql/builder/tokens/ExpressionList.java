package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

class ExpressionList implements Token {
  private final List<Expression> expressions = new ArrayList<>();
  private final String separator;


  ExpressionList(List<? extends Expression> expressions) {
    this(expressions, ",\n  ");
  }

  ExpressionList(Expression... expressions) {
    this(Arrays.stream(expressions).toList(), ",\n  ");
  }

  ExpressionList(List<? extends Expression> expressions, String separator) {
    this.expressions.addAll(expressions);
    this.separator = separator;
  }

  public static ExpressionList empty() {
    return new ExpressionList();
  }

  public static ExpressionList inline(Expression... expressions) {
    return new ExpressionList(Arrays.stream(expressions).toList(), ", ");
  }

  public static ExpressionList inline(List<Expression> expressions) {
    return new ExpressionList(expressions, ", ");
  }

  public void add(Expression expression) {
    expressions.add(expression);
  }

  private String render(Function<Expression, String> renderStrategy) {
    var joiner = new StringJoiner(separator);
    expressions.stream().map(renderStrategy).forEach(joiner::add);
    return joiner.toString();
  }

  @Override
  public String render(Dialect dialect) {
    return render(e -> e.render(dialect));
  }

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    expressions.forEach(e -> e.swapTable(oldTable, newTable));
  }

  public String renderDefinition(Dialect dialect) {
    return render(e -> e.renderDefinition(dialect));
  }

  public void pop() {
    this.expressions.remove(0);
  }

  public boolean merge(ExpressionList toMerge) {
    // TODO
    var hashByColumnId = expressions.stream()
        .collect(Collectors.toMap(
            Expression::columnName,
            Expression::contentHash));

    for (var expression : toMerge.expressions) {
      if (hashByColumnId.containsKey(expression.columnName())) {
        if (!hashByColumnId.get(expression.columnName()).equals(expression.contentHash())) {
          // column with same name but different definition, cannot merge
          return false;
        }
      }
    }

    toMerge.expressions.stream()
        .filter(e -> !hashByColumnId.containsKey(e.columnName()))
        .forEach(expressions::add);

    return true;
  }

  public List<Expression> expressions() {
    return expressions;
  }

  public boolean isEmpty() {
    return expressions.isEmpty();
  }

  public String referenceInGroupByOrderBy(Dialect dialect) {
    return render(e -> e.referenceInGroupByOrderBy(dialect));
  }
}
