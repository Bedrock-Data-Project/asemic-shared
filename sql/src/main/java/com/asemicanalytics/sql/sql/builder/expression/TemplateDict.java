package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.sql.sql.builder.Token;
import com.asemicanalytics.sql.sql.builder.tablelike.TableLike;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TemplateDict {
  private Map<String, ? extends Token> tokens;
  private Function<String, ? extends Token> onMissing;
  private Map<TableLike, TableLike> tableSwaps = new HashMap<>();

  public TemplateDict(Map<String, ? extends Token> tokens,
                      Function<String, ? extends Token> onMissing) {
    this.tokens = tokens;
    this.onMissing = onMissing;
  }

  public static TemplateDict noMissing(Map<String, ? extends Token> tokens) {
    return new TemplateDict(tokens, (key) -> {
      throw new IllegalArgumentException("token " + key + " not found. Tokens: " + tokens);
    });
  }

  public static TemplateDict empty() {
    return noMissing(Map.of());
  }

  public void swapTable(TableLike oldTable, TableLike newTable) {
    tableSwaps.put(oldTable, newTable);
  }

  public Token get(String key) {
    var token = tokens.get(key);
    if (token == null) {
      token = onMissing.apply(key);
    }
    Token finalToken = token;
    tableSwaps.forEach(finalToken::swapTable);
    return finalToken;
  }
}
