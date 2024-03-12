package com.asemicanalytics.sql.sql.builder.expression;

import com.asemicanalytics.core.Dialect;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatter {
  private static final Pattern PATTERN = Pattern.compile("\\{([^{}]+)}");

  public static Set<String> extractKeys(String input) {
    Set<String> extractedValues = new HashSet<>();
    Matcher matcher = PATTERN.matcher(input);
    while (matcher.find()) {
      String extractedValue = matcher.group(1);
      extractedValues.add(extractedValue);
    }
    return extractedValues;
  }

  public static String format(String input, TemplateDict dict, Dialect dialect) {
    var keys = extractKeys(input);
    String converted = input;

    for (String key : keys) {
      converted = converted.replace("{" + key + "}", dict.get(key).render(dialect));
    }

    return converted;
  }

}
