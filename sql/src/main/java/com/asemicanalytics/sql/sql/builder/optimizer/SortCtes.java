package com.asemicanalytics.sql.sql.builder.optimizer;

import com.asemicanalytics.sql.sql.builder.select.SelectStatement;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.LinkedHashMap;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class SortCtes implements OptimizationRule {

  @Override
  public void optimize(LinkedHashMap<String, Cte> ctes, SelectStatement selectStatement) {
    var ordered = topologicalSort(ctes);
    ctes.clear();
    ctes.putAll(ordered);
  }

  private LinkedHashMap<String, Cte> topologicalSort(LinkedHashMap<String, Cte> ctes) {
    Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
    for (var cte : ctes.values()) {
      g.addVertex(cte.name());
      cte.getDependentCtes().forEach((key, value) -> {
        g.addVertex(key);
        g.addEdge(key, cte.name());
      });
    }

    LinkedHashMap<String, Cte> ordered = new LinkedHashMap<>();
    var topogicalOrder = new TopologicalOrderIterator<>(g);
    while (topogicalOrder.hasNext()) {
      var cte = ctes.get(topogicalOrder.next());
      ordered.put(cte.name(), cte);
    }

    return ordered;
  }
}
