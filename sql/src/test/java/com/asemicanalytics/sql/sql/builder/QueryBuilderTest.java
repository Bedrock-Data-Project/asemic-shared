package com.asemicanalytics.sql.sql.builder;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.cte;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.table;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sql.sql.builder.tokens.Join;
import com.asemicanalytics.sql.sql.builder.tokens.JoinType;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueryBuilderTest {

  // TODO add more tests

  @Test
  void shouldConsolidateCtes_whenTwoCtesAreSame() {
    QueryBuilder queryBuilder = new QueryBuilder();
    var cte1 = cte(queryBuilder, "cte", select()
        .select(int_(1))
        .from(table(TableReference.of("table"))));
    var cte2 = cte(queryBuilder, "cte", select()
        .select(int_(1))
        .from(table(TableReference.of("table"))));

    queryBuilder.select(select()
        .select(int_(1))
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
    var table = table(TableReference.of("table"));

    var cte1 = cte(queryBuilder, "source", select()
        .select(table.column("c"))
        .from(table));
    var cte2 = cte(queryBuilder, "source", select()
        .select(table.column("c"))
        .from(table));

    var cteNext1 = cte(queryBuilder, "mid", select()
        .select(cte1.column("c"))
        .from(cte1)
        .and(cte1.column("c").condition("=", List.of("1"), DataType.INTEGER))
        .groupBy(cte1.column("c")));

    var cteNext2 = cte(queryBuilder, "mid", select()
        .select(cte2.column("c"))
        .from(cte2)
        .and(cte2.column("c").condition("=", List.of("2"), DataType.INTEGER))
        .groupBy(cte2.column("c")));

    queryBuilder.select(select()
        .select(cteNext1.column("c"), cteNext2.column("c"))
        .from(cteNext1).join(new Join(JoinType.INNER, cteNext2)));

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
    var table = table(TableReference.of("table"));

    var cte1 = cte(queryBuilder, "source", select()
        .select(table.column("c"))
        .from(table));
    var cte2 = cte(queryBuilder, "source", select()
        .select(table.column("c"))
        .from(table));

    var cteNext1 = cte(queryBuilder, "mid", select()
        .select(cte1.column("c"))
        .from(cte1)
        .and(cte1.column("c").condition("=", List.of("1"), DataType.INTEGER))
        .groupBy(cte1.column("c")));

    var cteNext2 = cte(queryBuilder, "mid", select()
        .select(cte2.column("c"))
        .from(cte2)
        .and(cte2.column("c").condition("=", List.of("1"), DataType.INTEGER))
        .groupBy(cte2.column("c")));

    queryBuilder.select(select()
        .select(cteNext1.column("c"), cteNext2.column("c"))
        .from(cteNext1).join(new Join(JoinType.INNER, cteNext2)));

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
