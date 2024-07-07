package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.tokens.Cte;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;
import java.util.List;

public class SqlQueryBuilder {
  public static SequenceQuery prepareCtes(Sequence sequence, DatetimeInterval datetimeInterval,
                                          List<String> includeColumns) {
    QueryBuilder queryBuilder = new QueryBuilder();

    Cte source =
        DomainCteBuilder.buildCte(sequence, queryBuilder, datetimeInterval, includeColumns);
    TableLike domain = source.select().from();
    TableLike sequences = SequencesCteBuilder.buildCte(sequence, queryBuilder, source);
    TableLike subsequences = SubsequencesCteBuilder.buildCte(sequence, queryBuilder,
        sequences, includeColumns);
    TableLike steps = StepsCteBuilder.buildCte(sequence, queryBuilder, subsequences);

    return new SequenceQuery(queryBuilder, domain, source, steps);
  }
}
