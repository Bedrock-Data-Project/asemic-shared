package com.asemicanalytics.sql.sql.builder.expression;


import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.Token;

public interface Expression extends Token {
  default String renderDefinition(Dialect dialect) {
    return render(dialect);
  }

  default String referenceInGroupByOrderBy(Dialect dialect) {
    return render(dialect);
  }

  default AliasedExpression withAlias(String alias) {
    return new AliasedExpression(this, alias);
  }

  default String contentHash() {
    return hashString(renderDefinition(contentHashDialect())).substring(0, 4);
  }

}
