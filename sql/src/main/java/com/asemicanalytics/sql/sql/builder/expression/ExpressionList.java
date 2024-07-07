package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExpressionList implements Token {
  private final List<Expression> expressions = new ArrayList<>();
  private final String separator;


  public ExpressionList(List<? extends Expression> expressions) {
    this(expressions, ",\n  ");
  }

  public ExpressionList(Expression... expressions) {
    this(Arrays.stream(expressions).toList(), ",\n  ");
  }

  public ExpressionList(List<? extends Expression> expressions, String separator) {
    this.expressions.addAll(expressions);
    this.separator = separator;
  }

  public static ExpressionList empty() {
    return new ExpressionList();
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

  public void merge(ExpressionList toMerge) {
    var current = expressions.stream()
        .map(Expression::contentHash)
        .collect(Collectors.toSet());

    toMerge.expressions.stream()
        .filter(e -> !current.contains(e.contentHash()))
        .forEach(expressions::add);
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
