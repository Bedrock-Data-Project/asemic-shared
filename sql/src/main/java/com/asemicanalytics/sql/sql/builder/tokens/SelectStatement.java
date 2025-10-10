package com.asemicanalytics.sql.sql.builder.tokens;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;

import com.asemicanalytics.core.Dialect;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class SelectStatement implements StandaloneStatement {
  public static final Expression SELECT_STAR = parse("*", TemplateDict.empty());
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
  private boolean ignoreJoinsInContentHash = false;

  SelectStatement() {

  }

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

  public void addSelect(Expression expression) {
    select.expressions().add(expression);
  }

  public void popSelect() {
    select.popExpression();
  }

  public void popOrderBy() {
    orderBy.expressions().pop();
  }

  public void popGroupBy() {
    groupBy.expressions().pop();
  }

  public SelectStatement select(Expression... expressions) {
    select = new Select(new ExpressionList(expressions));
    return this;
  }

  public Select select() {
    return select;
  }

  public SelectStatement select(List<Expression> expressions) {
    select = new Select(new ExpressionList(expressions));
    return this;
  }

  public SelectStatement selectStar() {
    return select(SELECT_STAR);
  }

  public SelectStatement join(Join join) {
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

  public SelectStatement groupBy(Expression... expressions) {
    groupBy = new GroupBy(new ExpressionList(expressions));
    return this;
  }

  public SelectStatement groupBy(List<Expression> expressions) {
    groupBy = new GroupBy(new ExpressionList(expressions));
    return this;
  }

  public SelectStatement having(BooleanExpression expression) {
    having = new Having(expression);
    return this;
  }

  ExpressionList orderBy() {
    return orderBy.expressions();
  }

  public SelectStatement orderBy(Expression... expressions) {
    orderBy = new OrderBy(new ExpressionList(expressions));
    return this;
  }

  public SelectStatement orderByDesc(Expression... expressions) {
    orderBy = new OrderBy(new ExpressionList(expressions), true);
    return this;
  }

  public SelectStatement limit(int limit) {
    this.limit = new Limit(limit);
    return this;
  }

  private String renderJoins(Dialect dialect) {
    Set<String> uniqueJoinStrings = new LinkedHashSet<>();
    joins.forEach(j -> uniqueJoinStrings.add(j.render(dialect)));
    var stringJoiner = new StringJoiner("\n");
    uniqueJoinStrings.forEach(stringJoiner::add);
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
  public String renderAfterCte(Dialect dialect) {
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
  public String renderBeforeCte(Dialect dialect) {
    return "";
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
    if (!ignoreJoinsInContentHash) {
      sb.append(renderJoins(contentHashDialect()));
    }
    if (where != null) {
      sb.append(where.contentHash());
    }
    if (qualify != null) {
      sb.append(qualify.contentHash());
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

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    joins.forEach(j -> j.swapTable(oldTable, newTable));

    if (select != null) {
      select.swapTable(oldTable, newTable);
    }
    if (from != null) {
      from.swapTable(oldTable, newTable);
    }
    if (where != null) {
      where.swapTable(oldTable, newTable);
    }
    if (qualify != null) {
      qualify.swapTable(oldTable, newTable);
    }
    if (groupBy != null) {
      groupBy.swapTable(oldTable, newTable);
    }
    if (having != null) {
      having.swapTable(oldTable, newTable);
    }
    if (orderBy != null) {
      orderBy.swapTable(oldTable, newTable);
    }
    if (limit != null) {
      limit.swapTable(oldTable, newTable);
    }
    if (unionAll != null) {
      unionAll.swapTable(oldTable, newTable);
    }
  }

  public Map<String, Cte> getDependentCtes() {
    LinkedHashMap<String, Cte> dependentCtes = new LinkedHashMap<>();

    if (from != null) {
      from.table().getDependantCte().ifPresent(cte -> dependentCtes.put(cte.name(), cte));
    }
    joins.forEach(join -> {
      join.table().getDependantCte().ifPresent(cte -> dependentCtes.put(cte.name(), cte));
    });

    if (unionAll != null) {
      dependentCtes.putAll(unionAll.getDependentCtes());
    }
    return dependentCtes;
  }

  public void setIgnoreJoinsInContentHash(boolean ignoreJoinsInContentHash) {
    this.ignoreJoinsInContentHash = ignoreJoinsInContentHash;
  }
}
