package com.asemicanalytics.sql.sql.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.select.Join;
import com.asemicanalytics.sql.sql.builder.select.JoinType;
import com.asemicanalytics.sql.sql.builder.select.SelectStatement;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import com.asemicanalytics.sql.sql.builder.tablelike.SimpleTable;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueryBuilderTest {

  // TODO add more tests

  @Test
  void shouldConsolidateCtes_whenTwoCtesAreSame() {
    QueryBuilder queryBuilder = new QueryBuilder();
    var cte1 = new Cte("cte", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(Constant.ofInt(1))
        .from(new SimpleTable(TableReference.of("table"))));
    var cte2 = new Cte("cte", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(Constant.ofInt(1))
        .from(new SimpleTable(TableReference.of("table"))));

    queryBuilder.with(cte1);
    queryBuilder.with(cte2);

    queryBuilder.select(new SelectStatement()
        .select(Constant.ofInt(1))
        .from(cte1));

    var expectedSql = """
        WITH cte AS (
        SELECT
          1
        FROM table
        )
        SELECT
          1
        FROM cte""";
    assertEquals(expectedSql, queryBuilder.render(new ContentHashDialect()));
  }

  @Test
  void shouldReplaceConsolidatedCte_whenTwoCtesAreSame() {
    QueryBuilder queryBuilder = new QueryBuilder();
    var table = new SimpleTable(TableReference.of("table"));

    var cte1 = new Cte("source", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(table.column("c"))
        .from(table));
    var cte2 = new Cte("source", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(table.column("c"))
        .from(table));

    var cteNext1 = new Cte("mid", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(cte1.column("c"))
        .from(cte1)
        .and(BooleanExpression.fromExpression(cte1.column("c"), "=", List.of("1"), DataType.INTEGER))
        .groupBy(cte1.column("c")));

    var cteNext2 = new Cte("mid", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(cte2.column("c"))
        .from(cte2)
        .and(BooleanExpression.fromExpression(cte2.column("c"), "=", List.of("2"), DataType.INTEGER))
        .groupBy(cte2.column("c")));

    queryBuilder.select(new SelectStatement()
        .select(cteNext1.column("c"), cteNext2.column("c"))
        .from(cteNext1).join(new Join(JoinType.INNER, cteNext2)));

    queryBuilder.with(cte1);
    queryBuilder.with(cte2);
    queryBuilder.with(cteNext1);
    queryBuilder.with(cteNext2);

    var expectedSql = """
        WITH source AS (
        SELECT
          table.c
        FROM table
        ),
        mid AS (
        SELECT
          source.c
        FROM source
        WHERE source.c = 1
        GROUP BY
          source.c
        ),
        mid_1 AS (
        SELECT
          source.c
        FROM source
        WHERE source.c = 2
        GROUP BY
          source.c
        )
        SELECT
          mid.c,
          mid_1.c
        FROM mid
        INNER JOIN mid_1""";
    assertEquals(expectedSql, queryBuilder.render(new ContentHashDialect()));
  }

  @Test
  void shouldReplaceConsolidatedCte_whenCtesBecomeSameAfterSwap() {
    QueryBuilder queryBuilder = new QueryBuilder();
    var table = new SimpleTable(TableReference.of("table"));

    var cte1 = new Cte("source", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(table.column("c"))
        .from(table));
    var cte2 = new Cte("source", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(table.column("c"))
        .from(table));

    var cteNext1 = new Cte("mid", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(cte1.column("c"))
        .from(cte1)
        .and(BooleanExpression.fromExpression(cte1.column("c"), "=", List.of("1"), DataType.INTEGER))
        .groupBy(cte1.column("c")));

    var cteNext2 = new Cte("mid", queryBuilder.nextCteIndex(), new SelectStatement()
        .select(cte2.column("c"))
        .from(cte2)
        .and(BooleanExpression.fromExpression(cte2.column("c"), "=", List.of("1"), DataType.INTEGER))
        .groupBy(cte2.column("c")));

    queryBuilder.select(new SelectStatement()
        .select(cteNext1.column("c"), cteNext2.column("c"))
        .from(cteNext1).join(new Join(JoinType.INNER, cteNext2)));

    queryBuilder.with(cte1);
    queryBuilder.with(cte2);
    queryBuilder.with(cteNext1);
    queryBuilder.with(cteNext2);

    var expectedSql = """
        WITH source AS (
        SELECT
          table.c
        FROM table
        ),
        mid AS (
        SELECT
          source.c
        FROM source
        WHERE source.c = 1
        GROUP BY
          source.c
        )
        SELECT
          mid.c,
          mid.c
        FROM mid
        INNER JOIN mid""";
    assertEquals(expectedSql, queryBuilder.render(new ContentHashDialect()));
  }
}
