package com.asemicanalytics.config.mapper.dtomapper.kpi;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.identifier;

import com.asemicanalytics.core.kpi.KpiComponent;
import com.asemicanalytics.core.kpi.KpixaxisConfig;
import com.asemicanalytics.sql.sql.builder.tokens.Formatter;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

public class KpiComponentsSplitter {

  private static final List<String>
      aggregationFunctions = List.of(
      "SUM", "AVG", "COUNT", "MIN", "MAX", "COUNTD"
  );

  private record Range(String aggregation, int start, int end) {
  }

  private int indexOfClosingBracket(String formula, int fromIndex) {
    int bracketCount = 0;
    for (int i = fromIndex; i < formula.length(); i++) {
      char c = formula.charAt(i);
      if (c == '(') {
        bracketCount++;
      } else if (c == ')') {
        bracketCount--;
        if (bracketCount == 0) {
          return i;
        }
      }
    }
    throw new IllegalArgumentException("Invalid formula, non matching brackets: " + formula);
  }

  private Optional<Range> getAggregationFunctionIndex(String formula) {
    int fromIndex = 0;

    for (String aggregationFunction : aggregationFunctions) {
      while (fromIndex != -1) {
        int index = formula.toUpperCase().indexOf(aggregationFunction + "(", fromIndex);
        if (index == -1) {
          fromIndex = 0;
          break;
        }

        if (index > 0) {
          char prevChar = formula.charAt(index - 1);
          if (Character.isAlphabetic(prevChar) || Character.isDigit(prevChar) || prevChar == '_') {
            fromIndex = index + 1;
            continue;
          }
        }

        return Optional.of(
            new Range(aggregationFunction, index, indexOfClosingBracket(formula, index)));
      }
    }
    return Optional.empty();
  }

  private void validateParentheses(String formula) {
    int count = 0;
    boolean valid = true;
    for (char c : formula.toCharArray()) {
      if (c == '(') {
        count++;
      } else if (c == ')') {
        if (count == 0) {
          valid = false;
          break;
        }
        count--;
      }
    }
    if (count != 0 || !valid) {
      throw new IllegalArgumentException("Invalid formula, non matching brackets: " + formula);
    }
  }

  private String trimOuterParentheses(String str) {
    while (str.startsWith("(") && str.endsWith(")")) {
      str = str.substring(1, str.length() - 1);
    }
    return str;
  }

  public KpixaxisConfig split(KpixaxisConfig kpixaxisConfig) {

    int maxIterations = 100;
    int componentIndex = 0;

    Map<String, KpiComponent> newComponents = new HashMap<>();
    Map<KpiComponent, String> newComponentsReverseIndex = new HashMap<>();

    String normalizedFormula = kpixaxisConfig.formula()
        .replaceAll("\\(\\s+", "(");
    validateParentheses(normalizedFormula);

    if (getAggregationFunctionIndex(normalizedFormula).isEmpty()) {
      throw new IllegalArgumentException(
          "Invalid formula, not a single aggregation: " + kpixaxisConfig.formula());
    }

    while (true) {
      if (maxIterations-- == 0) {
        throw new IllegalStateException("Too many iterations: "
            + kpixaxisConfig.formula());
      }

      Optional<Range> nextRange = getAggregationFunctionIndex(normalizedFormula);
      if (nextRange.isEmpty()) {
        break;
      }

      String extractedComponent = normalizedFormula.substring(
          nextRange.get().start(),
          nextRange.get().end() + 1);

      if (getAggregationFunctionIndex(
          extractedComponent.substring(nextRange.get().aggregation.length())).isPresent()) {
        throw new IllegalArgumentException("Invalid formula, nested aggregations: "
            + kpixaxisConfig.formula());
      }

      var properties = new ArrayList<>(Formatter.extractKeys(extractedComponent));
      var filters = buildFilters(kpixaxisConfig, properties);

      var kpiComponent = new KpiComponent(
          Formatter.format(extractedComponent,
              TemplateDict.noMissing(kpixaxisConfig.components().entrySet().stream().collect(
                  HashMap::new,
                  (map, entry) -> map.put(entry.getKey(), identifier(entry.getValue().select())),
                  HashMap::putAll)), null),
          filters);

      if (!newComponentsReverseIndex.containsKey(kpiComponent)) {
        newComponentsReverseIndex.put(kpiComponent, "component" + componentIndex);
        newComponents.put("component" + componentIndex, kpiComponent);
        componentIndex++;
      }

      normalizedFormula = normalizedFormula.replace(
          extractedComponent,
          "{" + newComponentsReverseIndex.get(kpiComponent) + "}");
    }

    var remainingComponents = Formatter.extractKeys(normalizedFormula);
    remainingComponents.removeAll(newComponents.keySet());
    if (!remainingComponents.isEmpty()) {
      throw new IllegalArgumentException("Invalid formula, non aggregated properties found: "
          + kpixaxisConfig.formula());
    }

    return new KpixaxisConfig(
        trimOuterParentheses(normalizedFormula),
        kpixaxisConfig.totalFunction(),
        newComponents
    );
  }

  private static TreeSet<String> buildFilters(KpixaxisConfig kpixaxisConfig,
                                              ArrayList<String> properties) {
    var filters = new TreeSet<String>();
    if (!properties.isEmpty()) {
      filters.addAll(kpixaxisConfig.components().get(properties.getFirst()).filters());
      for (int i = 1; i < properties.size(); i++) {
        if (!filters.equals(kpixaxisConfig.components().get(properties.get(i)).filters())) {
          throw new IllegalArgumentException("Invalid formula, conflicting filters: "
              + kpixaxisConfig.formula());
        }
      }
    }
    return filters;
  }
}
