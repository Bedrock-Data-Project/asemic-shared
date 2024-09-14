package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import java.time.LocalDate;
import java.util.List;

public class QueryFactory {
  public static Expression identifier(String identifier) {
    return new Identifier(identifier);
  }

  public static Cte cte(QueryBuilder queryBuilder, String name, SelectStatement select) {
    return cte(queryBuilder, name, select, List.of());
  }

  public static Cte cte(QueryBuilder queryBuilder, String name, SelectStatement select,
                        List<Cte> skipReuse) {
    var cte = new Cte(name, queryBuilder.nextCteIndex(), select);
    var contentHash = cte.contentHash();

    for (var existingCte : queryBuilder.ctes.values()) {
      if (skipReuse.stream().anyMatch(c -> c == existingCte)) {
        continue;
      }

      if (existingCte.contentHash().equals(contentHash)
          && existingCte.select().select().merge(cte.select().select())) {
        return existingCte;
      }
    }

    queryBuilder.put(cte);
    return cte;
  }

  public static TableLike table(TableReference table) {
    return new SimpleTable(table);
  }

  public static TableLike unnest(Expression arrayExpression, String alias) {
    return new Unnest(arrayExpression, alias);
  }

  public static BooleanExpression and(List<BooleanExpression> expressions) {
    var first = expressions.getFirst();
    for (BooleanExpression expression : expressions) {
      if (expression != first) {
        first.and(expression);
      }
    }
    return new BooleanExpressionGroup(first);
  }

  public static BooleanExpression or(List<BooleanExpression> expressions) {
    var first = expressions.getFirst();
    for (BooleanExpression expression : expressions) {
      if (expression != first) {
        first.or(expression);
      }
    }
    return new BooleanExpressionGroup(first);
  }

  public static CaseExpression case_(Expression expression) {
    return new CaseExpression(expression);
  }

  public static CaseExpression case_() {
    return new CaseExpression();
  }

  public static SelectStatement select() {
    return new SelectStatement();
  }

  public static WindowFunctionExpression window(Expression expression) {
    return new WindowFunctionExpression(expression);
  }

  public static Expression coalesce(Expression... expressions) {
    return new CoalesceExpression(expressions);
  }

  public static Expression coalesce(List<Expression> expressions) {
    return new CoalesceExpression(expressions.toArray(new Expression[0]));
  }

  public static Expression int_(long value) {
    return new Constant(Long.toString(value), DataType.INTEGER);
  }

  public static Expression string_(String value) {
    return new Constant(value, DataType.STRING);
  }

  public static Expression boolean_(boolean value) {
    return new Constant(Boolean.toString(value).toUpperCase(), DataType.BOOLEAN);
  }

  public static Expression date_(LocalDate value) {
    return new Constant(value.toString(), DataType.DATE);
  }

  public static Expression null_() {
    return new Constant(null, null);
  }

  public static Expression constant(String value, DataType dataType) {
    return new Constant(value, dataType);
  }

  public static Expression interval(int days) {
    return new DaysInterval(days);
  }

  public static Expression epochDays(Expression days) {
    return new EpochDays(days);
  }

  public static Expression dateDiff(Expression from, Expression to) {
    return new DateDiffExpression(from, to);
  }

  public static Expression function(String functionName, Expression... expressions) {
    return new FunctionExpression(functionName, expressions);
  }

  public static Expression function(String functionName, List<Expression> expressions) {
    return new FunctionExpression(functionName, expressions.toArray(new Expression[0]));
  }

  public static Expression intArray(Expression from, Expression to) {
    return new GenerateNumberArrayExpression(from, to);
  }

  public static Expression regex(Expression expression, String pattern) {
    return new RegexExpression(expression, pattern);
  }

  public static Expression parse(String expression, TemplateDict templateDict) {
    return new TemplatedExpression(expression, templateDict);
  }

  public static Expression truncate(Expression expression, TimeGrains timeGrain) {
    return new DateTruncatedExpression(expression, timeGrain);
  }

  public static Expression timestamp(Expression expression, int days) {
    return new ToTimestampExpression(expression, days);
  }

  public static Expression unixTimestamp(Expression expression) {
    return new ToUnixTimestamp(expression);
  }

  public static Token in(Expression... expressions) {
    return ExpressionList.inline(expressions);
  }

  public static Token in(List<Expression> expressions) {
    return ExpressionList.inline(expressions.toArray(new Expression[0]));
  }


}
