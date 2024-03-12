package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.sql.sql.builder.Token;
import java.util.Map;
import java.util.function.Function;

public record TemplateDict(
    Map<String, ? extends Token> tokens,
    Function<String, ? extends Token> onMissing
) {
  public static TemplateDict noMissing(Map<String, ? extends Token> tokens) {
    return new TemplateDict(tokens, (key) -> {
      throw new IllegalArgumentException("token " + key + " not found. Tokens: " + tokens);
    });
  }

  public static TemplateDict empty() {
    return noMissing(Map.of());
  }

  public Token get(String key) {
    var token = tokens.get(key);
    if (token != null) {
      return token;
    }
    return onMissing.apply(key);
  }
}
