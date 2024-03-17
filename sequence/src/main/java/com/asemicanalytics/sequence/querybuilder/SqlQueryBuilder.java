package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;

public class SqlQueryBuilder {
  public static SequenceQuery prepareCtes(Sequence sequence) {
    QueryBuilder queryBuilder = new QueryBuilder();

    Cte source = DomainCteBuilder.buildCte(sequence, queryBuilder);
    Cte sequences = SequencesCteBuilder.buildCte(sequence, queryBuilder, source);
    Cte subsequences = SubsequencesCteBuilder.buildCte(sequence, queryBuilder, sequences);
    Cte steps = StepsCteBuilder.buildCte(sequence, queryBuilder, subsequences);

    return new SequenceQuery(queryBuilder, source, steps);
  }

  public record SequenceQuery(QueryBuilder queryBuilder, Cte source, Cte steps) {
  }
}
