package com.asemicanalytics.sequence.querylanguage;

class AliasVisitor extends QueryLanguageBaseVisitor<String> {
  @Override
  public String visitDomainStepAlias(QueryLanguageParser.DomainStepAliasContext ctx) {
    return ctx.NAME().getText();
  }
}
