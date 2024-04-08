package com.asemicanalytics.sql.sql.builder;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.TreeMap;

public class QueryBuilder implements Token {

  private final TreeMap<String, Cte> ctes = new TreeMap<>();
  private SelectStatement mainStatement;
  private int cteIndex = 1;

  public Optional<Cte> getCompatibleCte(Cte cte) {
    String hash = cte.contentHash();
    return ctes.values().stream()
        .filter(c -> c.tag().equals(cte.tag()) && c.contentHash().equals(hash))
        .findFirst();
  }

  public QueryBuilder select(SelectStatement mainStatement) {
    this.mainStatement = mainStatement;
    return this;
  }

  public QueryBuilder with(Cte cte) {
    ctes.put(cte.name(), cte);
    return this;
  }

  @Override
  public String render(Dialect dialect) {
    var sb = new StringBuilder();
    if (!ctes.isEmpty()) {
      sb.append("WITH ");
      var joiner = new StringJoiner(",\n");
      topologicalSort().forEach(cte -> joiner.add(cte.renderDefinition(dialect)));
      sb.append(joiner);
      sb.append("\n");
    }
    sb.append(mainStatement.render(dialect));

    var cleanedtokens = Arrays.stream(sb.toString().split("\n"))
        .filter(l -> !l.isBlank()).toList();
    var joiner = new StringJoiner("\n");
    cleanedtokens.forEach(joiner::add);
    return joiner.toString();
  }

  private Collection<Cte> topologicalSort() {
    LinkedHashMap<String, Cte> ordered = new LinkedHashMap<>();
    LinkedList<Cte> queue = new LinkedList<>(this.mainStatement.getDependentCtes().values());

    while (!queue.isEmpty()) {
      for (int i = 0; i < queue.size(); i++) {
        var cte = queue.pop();
        ordered.put(cte.name(), cte);

        cte.getDependentCtes().forEach((key, value) -> {
          if (!ordered.containsKey(key)) {
            queue.add(value);
          }
        });
      }
    }

    // normalize indices
    Map<String, Integer> counts = new HashMap<>();
    Collection<Cte> reversed = ordered.reversed().values();
    for (var cte : reversed) {
      if (counts.containsKey(cte.tag())) {
        cte.setIndex(counts.get(cte.tag()));
        counts.put(cte.tag(), counts.get(cte.tag()) + 1);
      } else {
        counts.put(cte.tag(), 1);
        cte.setIndex(0);
      }
    }
    return reversed;
  }

  public int nextCteIndex() {
    return cteIndex++;
  }
}
