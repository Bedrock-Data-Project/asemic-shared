package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.core.PlaceholderKeysExtractor;
import java.util.Set;

public class Formatter {

  public static Set<String> extractKeys(String input) {
    return PlaceholderKeysExtractor.extractKeys(input);
  }

  public static String format(String input, TemplateDict dict, Dialect dialect) {
    var keys = extractKeys(input);
    String converted = input;

    for (String key : keys) {
      converted = converted.replace("{" + key + "}", dict.get(key).render(dialect));
    }

    return converted;
  }

  public static void validate(String input, TemplateDict dict) {
    extractKeys(input).forEach(dict::get);
  }

}
