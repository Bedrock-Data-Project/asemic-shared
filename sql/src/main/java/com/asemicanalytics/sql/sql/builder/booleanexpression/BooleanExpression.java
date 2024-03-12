package com.asemicanalytics.sql.sql.builder.booleanexpression;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BooleanExpression extends TemplatedExpression {
  protected BooleanExpressionNode nextNode;

  public BooleanExpression(TemplatedExpression expression) {
    super(expression.getExpression(), expression.getTemplateDict());
  }


  public static BooleanExpression fromExpression(Expression expression, String operator,
                                                 List<String> values, DataType dataType) {
    var constants = new ArrayList<>(values.stream().map(x -> new Constant(x, dataType)).toList());
    var valueList = new ExpressionList(constants, ", ");

    return switch (operator) {
      case "<", "<=", ">", ">=", "=", "!=" ->
          new BooleanExpression(new TemplatedExpression("{expression} " + operator + " {value}",
              TemplateDict.noMissing(Map.of("expression", expression, "value", constants.get(0)))));
      case "like" -> new BooleanExpression(new TemplatedExpression("{expression} LIKE {value}",
          TemplateDict.noMissing(Map.of("expression", expression, "value", constants.get(0)))));
      case "not_like" ->
          new BooleanExpression(new TemplatedExpression("{expression} NOT LIKE {value}",
              TemplateDict.noMissing(Map.of("expression", expression, "value", constants.get(0)))));
      case "in" -> new BooleanExpression(new TemplatedExpression("{expression} IN ({values})",
          TemplateDict.noMissing(Map.of("expression", expression, "values", valueList))));
      case "not_in" ->
          new BooleanExpression(new TemplatedExpression("{expression} NOT IN ({values})",
              TemplateDict.noMissing(Map.of("expression", expression, "values", valueList))));
      case "is_null" -> new BooleanExpression(new TemplatedExpression("{expression} IS NULL",
          TemplateDict.noMissing(Map.of("expression", expression))));
      case "is_not_null" ->
          new BooleanExpression(new TemplatedExpression("{expression} IS NOT NULL",
              TemplateDict.noMissing(Map.of("expression", expression))));
      case "boolean_is" -> new BooleanExpression(new TemplatedExpression("{expression} IS {value}",
          TemplateDict.noMissing(Map.of("expression", expression, "value", constants.get(0)))));
      case "boolean_is_not" ->
          new BooleanExpression(new TemplatedExpression("{expression} IS NOT {value}",
              TemplateDict.noMissing(Map.of("expression", expression, "value", constants.get(0)))));
      case "between" ->
          new BooleanExpression(new TemplatedExpression("{expression} BETWEEN {from} AND {to}",
              TemplateDict.noMissing(Map.of("expression", expression,
                  "from", constants.get(0), "to", constants.get(1)))));

      default -> throw new IllegalArgumentException("unsupported operator: " + operator);
    };
  }

  public static BooleanExpression fromDateInterval(Expression expression,
                                                   DatetimeInterval interval) {
    return BooleanExpression.fromExpression(expression, "between", List.of(
        interval.from().toLocalDate().toString(), interval.to().toLocalDate().toString()
    ), DataType.DATE);
  }

  public static BooleanExpression fromAnd(List<BooleanExpression> expressions) {
    var first = expressions.getFirst();
    for (BooleanExpression expression : expressions) {
      if (expression != first) {
        first.and(expression);
      }
    }
    return new BooleanExpressionGroup(first);
  }

  public static BooleanExpression fromOr(List<BooleanExpression> expressions) {
    var first = expressions.getFirst();
    for (BooleanExpression expression : expressions) {
      if (expression != first) {
        first.or(expression);
      }
    }
    return new BooleanExpressionGroup(first);
  }

  protected BooleanExpression tail() {
    var current = this;
    while (current.nextNode != null) {
      current = current.nextNode.getExpression();
    }
    return current;
  }

  public BooleanExpression and(BooleanExpression expression) {
    var tail = tail();
    tail.nextNode = new BooleanExpressionNode(BooleanOperator.AND, expression);
    return this;
  }

  public BooleanExpression or(BooleanExpression expression) {
    var tail = tail();
    tail.nextNode = new BooleanExpressionNode(BooleanOperator.OR, expression);
    return this;
  }

  protected String renderBaseExpression(Dialect dialect) {
    return super.render(dialect);
  }

  @Override
  public String render(Dialect dialect) {
    var tail = nextNode != null ? nextNode.render(dialect) : "";
    return renderBaseExpression(dialect) + tail;
  }
}
