package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;

public interface StandaloneStatement extends Token {
  default String render(Dialect dialect) {
    return renderBeforeCte(dialect) + "\n" + renderAfterCte(dialect);
  }

  String renderBeforeCte(Dialect dialect);

  String renderAfterCte(Dialect dialect);
}
