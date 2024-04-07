package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;

public record SequenceQuery(QueryBuilder queryBuilder, Cte domain, Cte source, Cte steps) {
}
