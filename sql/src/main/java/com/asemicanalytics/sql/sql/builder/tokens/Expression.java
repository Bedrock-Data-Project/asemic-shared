package com.asemicanalytics.sql.sql.builder.tokens;


import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.string_;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.DisconnectedDateIntervals;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Expression extends Token {
  default String renderDefinition(Dialect dialect) {
    return render(dialect);
  }

  default String referenceInGroupByOrderBy(Dialect dialect) {
    return render(dialect);
  }

  default Expression withAlias(String alias) {
    return new AliasedExpression(this, alias);
  }

  default String contentHash() {
    return hashString(renderDefinition(contentHashDialect())).substring(0, 4);
  }

  default BooleanExpression condition(String operator, Expression e) {
    return new BooleanExpression(parse("{expression} " + operator + " {e}",
        TemplateDict.noMissing(Map.of("expression", this, "e", e))));
  }

  default BooleanExpression condition(String operator, List<String> values, DataType dataType) {
    var constants = new ArrayList<>(values.stream().map(x -> new Constant(x, dataType)).toList());
    var valueList = new ExpressionList(constants, ", ");

    return switch (operator) {
      case "<", "<=", ">", ">=", "=", "!=" ->
          new BooleanExpression(parse("{expression} " + operator + " {value}",
              TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));

      // TODO
      case "any<", "any<=", "any>", "any>=", "any=", "any!=" ->
          new BooleanExpression(parse("{expression} " + operator + " {value}",
              TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));
      case "all<", "all<=", "all>", "all>=", "all=", "all!=" ->
          new BooleanExpression(parse("{expression} " + operator + " {value}",
              TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));

      case "like" -> new BooleanExpression(parse("{expression} LIKE {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));
      case "not_like" -> new BooleanExpression(parse("{expression} NOT LIKE {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));
      case "contains" -> new BooleanExpression(parse("{expression} LIKE {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value",
              string_("%" + values.get(0) + "%")))));
      case "not_contains" -> new BooleanExpression(parse("{expression} NOT LIKE {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value",
              string_("%" + values.get(0) + "%")))));
      case "regex" -> new BooleanExpression(parse("{expression}",
          TemplateDict.noMissing(Map.of("expression",
              new RegexExpression(this, values.get(0))))));
      case "in" -> new BooleanExpression(parse("{expression} IN ({values})",
          TemplateDict.noMissing(Map.of("expression", this, "values", valueList))));
      case "not_in" -> new BooleanExpression(parse("{expression} NOT IN ({values})",
          TemplateDict.noMissing(Map.of("expression", this, "values", valueList))));
      case "is_null" -> new BooleanExpression(parse("{expression} IS NULL",
          TemplateDict.noMissing(Map.of("expression", this))));
      case "is_not_null" -> new BooleanExpression(parse("{expression} IS NOT NULL",
          TemplateDict.noMissing(Map.of("expression", this))));
      case "boolean_is" -> new BooleanExpression(parse("{expression} IS {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));
      case "is_true" -> new BooleanExpression(parse("{expression} IS TRUE",
          TemplateDict.noMissing(Map.of("expression", this))));
      case "is_false" -> new BooleanExpression(parse("{expression} IS FALSE",
          TemplateDict.noMissing(Map.of("expression", this))));
      case "boolean_is_not" -> new BooleanExpression(parse("{expression} IS NOT {value}",
          TemplateDict.noMissing(Map.of("expression", this, "value", constants.get(0)))));
      case "between" -> new BooleanExpression(parse("{expression} BETWEEN {from} AND {to}",
          TemplateDict.noMissing(Map.of("expression", this,
              "from", constants.get(0), "to", constants.get(1)))));

      default -> throw new IllegalArgumentException("unsupported operator: " + operator);
    };
  }

  default BooleanExpression between(DatetimeInterval interval) {
    return condition("between", List.of(
        interval.from().toLocalDate().toString(), interval.to().toLocalDate().toString()
    ), DataType.DATE);
  }

  default BooleanExpression between(DisconnectedDateIntervals intervals) {
    return QueryFactory.or(intervals.intervals().stream()
        .map(i -> condition("between", List.of(
            i.from().toString(), i.to().toString()
        ), DataType.DATE))
        .toList());
  }

  default BooleanExpression asCondition() {
    return new BooleanExpression(this);
  }

  default Expression plusDays(int days) {
    return new DateAddExpression(this, days);
  }

  default Expression plusDays(Expression days) {
    return new DateAddExpression(this, days);
  }

  default String columnName() {
    if (this instanceof AliasedExpression) {
      return ((AliasedExpression) this).alias();
    }
    if (this instanceof TableColumn) {
      return ((TableColumn) this).name();
    }

    throw new IllegalArgumentException("Unsupported expression type: " + getClass().getName());
  }
}
