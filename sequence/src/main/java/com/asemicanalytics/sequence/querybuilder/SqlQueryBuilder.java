package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.List;

public class SqlQueryBuilder {
  public static SequenceQuery prepareCtes(Sequence sequence, DatetimeInterval datetimeInterval,
                                          List<String> includeColumns) {
    QueryBuilder queryBuilder = new QueryBuilder();

    Cte source =
        DomainCteBuilder.buildCte(sequence, queryBuilder, datetimeInterval, includeColumns);
    Cte domain = (Cte) source.select().from();
    Cte sequences = SequencesCteBuilder.buildCte(sequence, queryBuilder, source);
    Cte subsequences = SubsequencesCteBuilder.buildCte(sequence, queryBuilder,
        sequences, includeColumns);
    Cte steps = StepsCteBuilder.buildCte(sequence, queryBuilder, subsequences);

    return new SequenceQuery(queryBuilder, domain, source, steps);
  }
}
