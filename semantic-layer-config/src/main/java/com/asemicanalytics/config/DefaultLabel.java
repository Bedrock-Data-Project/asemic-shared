package com.asemicanalytics.config;

import java.util.Optional;

public class DefaultLabel {
  public static String of(Optional<String> label, String id) {
    return label.orElse(capitalize(id.replace("_", " ")));
  }

  private static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    String[] words = str.split(" ");
    StringBuilder capitalized = new StringBuilder();

    for (String word : words) {
      capitalized.append(word.substring(0, 1).toUpperCase());
      capitalized.append(word.substring(1).toLowerCase());
      capitalized.append(" ");
    }

    return capitalized.toString().trim();
  }
}
