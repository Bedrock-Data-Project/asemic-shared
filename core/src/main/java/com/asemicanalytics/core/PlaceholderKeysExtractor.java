package com.asemicanalytics.core;

import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderKeysExtractor {
  private static final Pattern PATTERN = Pattern.compile("\\{([^{}]+)}");

  public static Set<String> extractKeys(String input) {
    SequencedSet<String> extractedValues = new LinkedHashSet<>();
    Matcher matcher = PATTERN.matcher(input);
    while (matcher.find()) {
      String extractedValue = matcher.group(1);
      extractedValues.add(extractedValue);
    }
    return extractedValues;
  }
}
