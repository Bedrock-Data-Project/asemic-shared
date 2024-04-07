package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.join.Join;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SelectStatement implements Token {
  public static final Expression SELECT_STAR = new TemplatedExpression("*", TemplateDict.empty());
  private final List<Join> joins = new ArrayList<>();
  private Select select = new Select(ExpressionList.empty());
  private From from;
  private Where where;
  private Qualify qualify;
  private GroupBy groupBy = new GroupBy(ExpressionList.empty());
  private Having having;
  private OrderBy orderBy = new OrderBy(ExpressionList.empty());
  private Limit limit;
  private SelectStatement unionAll;

  public TableLike table() {
    return from.table();
  }

  public BooleanExpression where() {
    return where.booleanExpression();
  }

  public boolean hasJoin(Join join) {
    var renderedJoin = join.contentHash();
    for (var existingJoin : joins()) {
      if (existingJoin.contentHash().equals(renderedJoin)) {
        return true;
      }
    }
    return false;
  }

  public List<Join> joins() {
    return joins;
  }

  public TableLike from() {
    return from == null ? null : from.table();
  }

  public SelectStatement from(TableLike tableLike) {
    from = new From(tableLike);
    return this;
  }

  public Select select() {
    return select;
  }

  public SelectStatement select(ExpressionList expressions) {
    select = new Select(expressions);
    return this;
  }

  public SelectStatement select(Expression... expressions) {
    select = new Select(new ExpressionList(expressions));
    return this;
  }

  public SelectStatement selectStar() {
    return select(SELECT_STAR);
  }

  public SelectStatement join(Join join) {
    if (joins.stream().map(Join::contentHash).anyMatch(join.contentHash()::equals)) {
      return this;
    }
    joins.add(join);
    return this;
  }

  public SelectStatement join(List<Join> joins) {
    joins.forEach(this::join);
    return this;
  }

  public SelectStatement and(BooleanExpression booleanExpression) {
    if (where != null) {
      where.booleanExpression().and(booleanExpression);
    } else {
      where = new Where(booleanExpression);
    }
    return this;
  }

  public SelectStatement andQualify(BooleanExpression booleanExpression) {
    if (qualify != null) {
      qualify.booleanExpression().and(booleanExpression);
    } else {
      qualify = new Qualify(booleanExpression);
    }
    return this;
  }


  public SelectStatement or(BooleanExpression booleanExpression) {
    if (where != null) {
      where.booleanExpression().or(booleanExpression);
    } else {
      where = new Where(booleanExpression);
    }
    return this;
  }

  public ExpressionList groupBy() {
    return groupBy.expressions();
  }

  public SelectStatement groupBy(ExpressionList expressions) {
    groupBy = new GroupBy(expressions);
    return this;
  }

  public SelectStatement having(BooleanExpression expression) {
    having = new Having(expression);
    return this;
  }

  public ExpressionList orderBy() {
    return orderBy.expressions();
  }

  public SelectStatement orderBy(ExpressionList expressions) {
    orderBy = new OrderBy(expressions);
    return this;
  }

  public SelectStatement orderByDesc(ExpressionList expressions) {
    orderBy = new OrderBy(expressions, true);
    return this;
  }

  public SelectStatement limit(int limit) {
    this.limit = new Limit(limit);
    return this;
  }

  private String renderJoins(Dialect dialect) {
    var stringJoiner = new StringJoiner("\n");
    joins.forEach(j -> stringJoiner.add(j.render(dialect)));
    return stringJoiner.toString();
  }

  public SelectStatement unionAll(SelectStatement selectStatement) {
    if (unionAll != null) {
      unionAll.unionAll(selectStatement);
    } else {
      unionAll = selectStatement;
    }
    return this;
  }

  @Override
  public String render(Dialect dialect) {
    var sb = new StringBuilder();
    sb.append(select.render(dialect));
    if (from != null) {
      sb.append("\n").append(from.render(dialect));
    }
    sb.append("\n").append(renderJoins(dialect));
    if (where != null) {
      sb.append("\n").append(where.render(dialect));
    }
    if (groupBy != null && groupBy.expressions().expressions().size() > 0) {
      sb.append("\n").append(groupBy.render(dialect));
    }
    if (having != null) {
      sb.append("\n").append(having.render(dialect));
    }
    if (qualify != null) {
      sb.append("\n").append(qualify.render(dialect));
    }
    if (orderBy != null && orderBy.expressions().expressions().size() > 0) {
      sb.append("\n").append(orderBy.render(dialect));
    }
    if (limit != null) {
      sb.append("\n").append(limit.render(dialect));
    }
    if (unionAll != null) {
      sb.append("\n").append("UNION ALL\n").append(unionAll.render(dialect));
    }
    return sb.toString();
  }

  @Override
  public String contentHash() {
    var sb = new StringBuilder();

    // only columns that match group by columns are considered
    select.expressions().stream()
        .limit(groupBy == null ? 0 : groupBy.expressions().expressions().size())
        .forEach(e -> sb.append(e.contentHash()));

    if (from != null) {
      sb.append(from.contentHash());
    }
    sb.append(renderJoins(contentHashDialect()));
    if (where != null) {
      sb.append(where.contentHash());
    }
    if (groupBy != null) {
      sb.append(groupBy.contentHash());
    }
    if (having != null) {
      sb.append(having.contentHash());
    }
    if (orderBy != null) {
      sb.append(orderBy.contentHash());
    }
    if (limit != null) {
      sb.append(limit.contentHash());
    }
    if (unionAll != null) {
      sb.append(unionAll.contentHash());
    }
    return sb.toString();
  }

  public Map<String, Cte> getDependentCtes() {
    LinkedHashMap<String, Cte> dependentCtes = new LinkedHashMap<>();
    if (from != null && from.table() instanceof Cte) {
      Cte cte = (Cte) from.table();
      dependentCtes.put(cte.name(), cte);
    }
    joins.forEach(join -> {
      if (join.table() instanceof Cte) {
        Cte cte = (Cte) join.table();
        dependentCtes.put(cte.name(), cte);
      }
    });
    return dependentCtes;
  }
}
