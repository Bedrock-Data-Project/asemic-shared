package com.asemicanalytics.sequence.querybuilder;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.cte;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.function;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.window;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.QueryFactory;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class SequencesCteBuilder {
  public static final String SEQUENCE_COLUMN = "sequence";

  public static TableLike buildCte(Sequence sequence, QueryBuilder queryBuilder,
                                   TableLike domainCte) {
    if (sequence.isStartStepRepeated()) {
      throw new UnsupportedOperationException();
    } else {
      return buildIfStartStepNotRepeated(sequence, queryBuilder, domainCte);
    }
  }

  private static TableLike buildIfStartStepNotRepeated(Sequence sequence, QueryBuilder queryBuilder,
                                                       TableLike domainCte) {

    var stepValues = sequence.getSteps().get(0).getStepNames().stream()
        .map(QueryFactory::string_)
        .findFirst().get();

    var windowAggregation = function("SUM",
        parse("{step_column} = {step_value}",
            TemplateDict.noMissing(Map.of(
                "step_column", domainCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                "step_value", stepValues
            ))).asCondition().if_(int_(1), int_(0)));

    var columns = domainCte.columnNames().stream()
        .map(domainCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(window(windowAggregation)
        .partitionBy(domainCte.column(DomainCteBuilder.USER_ID_COLUMN))
        .orderBy(domainCte.column(DomainCteBuilder.STEP_TS_COLUMN))
        .withAlias(SEQUENCE_COLUMN));

    return cte(queryBuilder, "sequence_sequences",
        select()
            .select(columns)
            .from(domainCte)
    );
  }
}
