package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;

public record SequenceQuery(QueryBuilder queryBuilder, TableLike domain, TableLike source,
                            TableLike steps) {
}
