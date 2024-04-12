package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.expression.Expression;
import com.asemicanalytics.sql.sql.builder.expression.FunctionExpression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import java.util.Map;
import java.util.stream.Collectors;

class ExpressionVisitor extends QueryLanguageBaseVisitor<Expression> {
  @Override
  public Expression visitLiteral(QueryLanguageParser.LiteralContext ctx) {
    if (ctx.STRING_LITERAL() != null) {
      return Constant.ofString(ctx.STRING_LITERAL().getText());
    } else {
      return new Constant(ctx.getText(), DataType.INTEGER); // hack as number is rendered as it
    }
  }

  @Override
  public Expression visitParamName(QueryLanguageParser.ParamNameContext ctx) {
    return new Constant(ctx.getText(), DataType.INTEGER);
  }

  @Override
  public Expression visitUnaryOperator(QueryLanguageParser.UnaryOperatorContext ctx) {
    String prefix = ctx.getText();
    if (ctx.NOT() != null) {
      prefix = prefix + " ";
    }
    return new Constant(prefix, null);
  }

  @Override
  public Expression visitUnaryExpression(QueryLanguageParser.UnaryExpressionContext ctx) {
    return new TemplatedExpression("{prefix}{expression}", TemplateDict.noMissing(Map.of(
        "prefix", visit(ctx.unaryOperator()),
        "expression", visit(ctx.expression()))));
  }

  @Override
  public Expression visitMultiplicativeExpression(
      QueryLanguageParser.MultiplicativeExpressionContext ctx) {
    return new TemplatedExpression("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", new Constant(ctx.getChild(1).getText(), null),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitAdditiveExpression(QueryLanguageParser.AdditiveExpressionContext ctx) {
    return new TemplatedExpression("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", new Constant(ctx.getChild(1).getText(), null),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitComparativeExpression(
      QueryLanguageParser.ComparativeExpressionContext ctx) {
    return new TemplatedExpression("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", new Constant(ctx.getChild(1).getText(), null),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitAndExpression(QueryLanguageParser.AndExpressionContext ctx) {
    return new TemplatedExpression("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", new Constant(ctx.getChild(1).getText(), null),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitOrExpression(QueryLanguageParser.OrExpressionContext ctx) {
    return new TemplatedExpression("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", new Constant(ctx.getChild(1).getText(), null),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitIsNullExpression(QueryLanguageParser.IsNullExpressionContext ctx) {
    return new TemplatedExpression("{expression} IS {not}NULL",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression()),
            "not", new Constant(ctx.NOT() == null ? "" : ctx.NOT().getText() + " ", null))));
  }

  @Override
  public Expression visitParenExpression(QueryLanguageParser.ParenExpressionContext ctx) {
    return new TemplatedExpression("({expression})", TemplateDict.noMissing(Map.of(
        "expression", visit(ctx.expression()))));
  }

  @Override
  public Expression visitFunctionExpression(QueryLanguageParser.FunctionExpressionContext ctx) {
    return new FunctionExpression(ctx.functionName().getText(), new ExpressionList(
        ctx.expression().stream().map(this::visit).collect(Collectors.toList())));
  }

  @Override
  public Expression visitBetweenExpression(QueryLanguageParser.BetweenExpressionContext ctx) {
    return new TemplatedExpression("{expression} {not}BETWEEN {from} AND {to}",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression(0)),
            "not", new Constant(ctx.NOT() == null ? "" : ctx.NOT().getText() + " ", null),
            "from", visit(ctx.expression(1)),
            "to", visit(ctx.expression(2)))));
  }

  @Override
  public Expression visitInExpression(QueryLanguageParser.InExpressionContext ctx) {

    return new TemplatedExpression("{expression} {not}IN ({values})",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression(0)),
            "not", new Constant(ctx.NOT() == null ? "" : ctx.NOT().getText() + " ", null),
            "values", new ExpressionList(
                ctx.expression().stream()
                    .skip(1)
                    .map(this::visit)
                    .collect(Collectors.toList()), ", "))));
  }
}
