package com.asemicanalytics.sql.sql.builder.tokens;

import com.asemicanalytics.core.Dialect;
import com.asemicanalytics.sql.sql.builder.optimizer.SimplifyCteNames;
import com.asemicanalytics.sql.sql.builder.optimizer.SortCtes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class QueryBuilder implements Token {

  final LinkedHashMap<String, Cte> ctes = new LinkedHashMap<>();
  private SelectStatement mainStatement;
  private int cteIndex = 1;

  public QueryBuilder select(SelectStatement mainStatement) {
    this.mainStatement = mainStatement;
    return this;
  }

  public QueryBuilder put(Cte cte) {
    ctes.put(cte.name(), cte);
    return this;
  }

  @Override
  public String render(Dialect dialect) {
    new SortCtes().optimize(ctes, mainStatement);
    new SimplifyCteNames().optimize(ctes, mainStatement);


    var sb = new StringBuilder();
    if (!ctes.isEmpty()) {
      sb.append("WITH ");
      var joiner = new StringJoiner(",\n");
      ctes.values().forEach(cte -> joiner.add(cte.renderDefinition(dialect)));
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

  @Override
  public void swapTable(TableLike oldTable, TableLike newTable) {
    mainStatement.swapTable(oldTable, newTable);
    ctes.values().forEach(cte -> cte.swapTable(oldTable, newTable));
  }

  int nextCteIndex() {
    return cteIndex++;
  }

  public Map<String, Cte> getCtes() {
    return new HashMap<>(ctes);
  }
}
