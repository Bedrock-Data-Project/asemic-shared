package com.asemicanalytics.sequence.querybuilder;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.cte;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.function;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.unixTimestamp;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.window;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubsequencesCteBuilder {

  public static final String SUBSEQUENCE_COLUMN = "subsequence";
  public static final String REPEATITIONS_COLUMN = "repetitions";
  public static final String REPEATITION_COLUMN = "repetition";
  public static final String NEW_SUBSEQUENCE_COLUMN = "new_subsequence";

  public static TableLike buildCte(Sequence sequence, QueryBuilder queryBuilder,
                                   TableLike sequencesCte,
                                   List<String> includeColumns) {
    var timeHorizonCte = buildTimeHorizonCte(sequence, queryBuilder, sequencesCte);
    var subsequencePrepCte = buildSubsequencePrepCte(queryBuilder, timeHorizonCte);
    var subsequenceCte = buildSubsequenceCte(queryBuilder, subsequencePrepCte, includeColumns);
    return buildRepeatedActionsCte(queryBuilder, subsequenceCte);
  }

  private static TableLike buildTimeHorizonCte(Sequence sequence, QueryBuilder queryBuilder,
                                               TableLike sequencesCte) {
    var columns = sequencesCte.columnNames().stream()
        .map(sequencesCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    return cte(queryBuilder, "sequence_time_horizon",
        select()
            .select(columns)
            .from(sequencesCte)
            .andQualify(parse(
                "{ts} - {window} <= {horizon}",
                TemplateDict.noMissing(Map.of(
                    "ts", unixTimestamp(
                        sequencesCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
                    "window", unixTimestamp(
                        window(
                            function("MIN",
                                sequencesCte.column(DomainCteBuilder.STEP_TS_COLUMN)))
                            .partitionBy(
                                sequencesCte.column(DomainCteBuilder.USER_ID_COLUMN),
                                sequencesCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)
                            )),
                    "horizon", int_(sequence.getTimeHorizon().toSeconds())
                ))).asCondition()));
  }

  private static TableLike buildSubsequencePrepCte(QueryBuilder queryBuilder,
                                                   TableLike timehorizonCte) {
    var columns = timehorizonCte.columnNames().stream()
        .map(timehorizonCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(parse("{sequence} = 0 OR {action} = {window}",
        TemplateDict.noMissing(Map.of(
            "sequence", timehorizonCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
            "action", timehorizonCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
            "window", window(
                function("LAG",
                    timehorizonCte.column(DomainCteBuilder.STEP_NAME_COLUMN))
            ).partitionBy(
                timehorizonCte.column(DomainCteBuilder.USER_ID_COLUMN),
                timehorizonCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)
            ).orderBy(
                timehorizonCte.column(DomainCteBuilder.STEP_TS_COLUMN)
            )
        )))
        .asCondition()
        .if_(int_(0), int_(1))
        .withAlias(NEW_SUBSEQUENCE_COLUMN));

    return cte(queryBuilder, "sequence_sequences_prep",
        select()
            .select(columns)
            .from(timehorizonCte)
    );
  }

  private static TableLike buildSubsequenceCte(QueryBuilder queryBuilder,
                                               TableLike subsequencePrepCte,
                                               List<String> includeColumns) {

    var columns = subsequencePrepCte.columnNames().stream()
        .filter(c -> !c.equals(NEW_SUBSEQUENCE_COLUMN))
        .filter(c -> !includeColumns.contains(c))
        .map(subsequencePrepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(window(function("SUM",
        subsequencePrepCte.column(NEW_SUBSEQUENCE_COLUMN)))
        .partitionBy(
            subsequencePrepCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequencePrepCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)
        )
        .orderBy(
            subsequencePrepCte.column(DomainCteBuilder.STEP_TS_COLUMN)
        )
        .withAlias(SUBSEQUENCE_COLUMN));

    includeColumns.forEach(column -> columns.add(window(function("MIN",
        subsequencePrepCte.column(column)))
        .partitionBy(
            subsequencePrepCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequencePrepCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)
        )
        .orderBy(
            subsequencePrepCte.column(DomainCteBuilder.STEP_TS_COLUMN)
        )
        .withAlias(column)));

    return cte(queryBuilder, "sequence_subsequences",
        select()
            .select(columns)
            .from(subsequencePrepCte)
    );
  }

  private static TableLike buildRepeatedActionsCte(QueryBuilder queryBuilder,
                                                   TableLike subsequenceCte) {
    var columns = subsequenceCte.columnNames().stream()
        .map(subsequenceCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(window(function("COUNT", parse("*", TemplateDict.empty())))
        .partitionBy(
            subsequenceCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequenceCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
            subsequenceCte.column(SUBSEQUENCE_COLUMN)
        )
        .withAlias(REPEATITIONS_COLUMN));

    columns.add(window(function("ROW_NUMBER"))
        .partitionBy(
            subsequenceCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequenceCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
            subsequenceCte.column(SUBSEQUENCE_COLUMN)
        )
        .orderBy(
            subsequenceCte.column(DomainCteBuilder.STEP_TS_COLUMN)
        )
        .withAlias(REPEATITION_COLUMN));

    return cte(queryBuilder, "sequence_repeated_actions",
        select()
            .select(columns)
            .from(subsequenceCte)
    );
  }
}
