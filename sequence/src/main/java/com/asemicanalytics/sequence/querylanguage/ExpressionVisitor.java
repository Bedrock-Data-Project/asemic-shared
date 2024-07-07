package com.asemicanalytics.sequence.querylanguage;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.function;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.identifier;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.in;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.string_;

import com.asemicanalytics.sql.sql.builder.tokens.Expression;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.Map;
import java.util.stream.Collectors;

class ExpressionVisitor extends QueryLanguageBaseVisitor<Expression> {

  private final TableLike tableLike;

  ExpressionVisitor(TableLike tableLike) {
    this.tableLike = tableLike;
  }

  @Override
  public Expression visitLiteral(QueryLanguageParser.LiteralContext ctx) {
    if (ctx.STRING_LITERAL() != null) {
      return string_(ctx.STRING_LITERAL()
          .getText().substring(1, ctx.STRING_LITERAL().getText().length() - 1));
    } else {
      return identifier(ctx.getText()); // hack as number is rendered as it
    }
  }

  @Override
  public Expression visitParamName(QueryLanguageParser.ParamNameContext ctx) {
    return tableLike.column(ctx.getText());
  }

  @Override
  public Expression visitUnaryOperator(QueryLanguageParser.UnaryOperatorContext ctx) {
    String prefix = ctx.getText();
    if (ctx.NOT() != null) {
      prefix = prefix + " ";
    }
    return identifier(prefix);
  }

  @Override
  public Expression visitUnaryExpression(QueryLanguageParser.UnaryExpressionContext ctx) {
    return parse("{prefix}{expression}", TemplateDict.noMissing(Map.of(
        "prefix", visit(ctx.unaryOperator()),
        "expression", visit(ctx.expression()))));
  }

  @Override
  public Expression visitMultiplicativeExpression(
      QueryLanguageParser.MultiplicativeExpressionContext ctx) {
    return parse("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", identifier(ctx.getChild(1).getText()),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitAdditiveExpression(QueryLanguageParser.AdditiveExpressionContext ctx) {
    return parse("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", identifier(ctx.getChild(1).getText()),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitComparativeExpression(
      QueryLanguageParser.ComparativeExpressionContext ctx) {
    return parse("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", identifier(ctx.getChild(1).getText()),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitAndExpression(QueryLanguageParser.AndExpressionContext ctx) {
    return parse("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", identifier(ctx.getChild(1).getText()),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitOrExpression(QueryLanguageParser.OrExpressionContext ctx) {
    return parse("{left} {operator} {right}", TemplateDict.noMissing(Map.of(
        "left", visit(ctx.expression(0)),
        "operator", identifier(ctx.getChild(1).getText()),
        "right", visit(ctx.expression(1)))));
  }

  @Override
  public Expression visitIsNullExpression(QueryLanguageParser.IsNullExpressionContext ctx) {
    return parse("{expression} IS {not}NULL",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression()),
            "not", identifier(ctx.NOT() == null ? "" : ctx.NOT().getText() + " "))));
  }

  @Override
  public Expression visitParenExpression(QueryLanguageParser.ParenExpressionContext ctx) {
    return parse("({expression})", TemplateDict.noMissing(Map.of(
        "expression", visit(ctx.expression()))));
  }

  @Override
  public Expression visitFunctionExpression(QueryLanguageParser.FunctionExpressionContext ctx) {
    return function(ctx.functionName().getText(),
        ctx.expression().stream().map(this::visit).collect(Collectors.toList()));
  }

  @Override
  public Expression visitBetweenExpression(QueryLanguageParser.BetweenExpressionContext ctx) {
    return parse("{expression} {not}BETWEEN {from} AND {to}",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression(0)),
            "not", identifier(ctx.NOT() == null ? "" : ctx.NOT().getText() + " "),
            "from", visit(ctx.expression(1)),
            "to", visit(ctx.expression(2)))));
  }

  @Override
  public Expression visitInExpression(QueryLanguageParser.InExpressionContext ctx) {

    return parse("{expression} {not}IN ({values})",
        TemplateDict.noMissing(Map.of(
            "expression", visit(ctx.expression(0)),
            "not", identifier(ctx.NOT() == null ? "" : ctx.NOT().getText() + " "),
            "values", in(
                ctx.expression().stream()
                    .skip(1)
                    .map(this::visit)
                    .collect(Collectors.toList())))));
  }
}
