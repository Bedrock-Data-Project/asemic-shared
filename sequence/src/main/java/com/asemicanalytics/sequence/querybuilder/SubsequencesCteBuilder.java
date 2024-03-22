package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.expression.FunctionExpression;
import com.asemicanalytics.sql.sql.builder.expression.IfExpression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.expression.ToUnixTimestamp;
import com.asemicanalytics.sql.sql.builder.expression.windowfunction.WindowFunctionExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubsequencesCteBuilder {

  public static final String SUBSEQUENCE_COLUMN = "subsequence";
  public static final String REPEATITIONS_COLUMN = "repetitions";
  public static final String REPEATITION_COLUMN = "repetition";
  public static final String NEW_SUBSEQUENCE_COLUMN = "new_subsequence";

  public static Cte buildCte(Sequence sequence, QueryBuilder queryBuilder, Cte sequencesCte) {
    var timeHorizonCte = buildTimeHorizonCte(sequence, queryBuilder, sequencesCte);
    var subsequencePrepCte = buildSubsequencePrepCte(queryBuilder, timeHorizonCte);
    var subsequenceCte = buildSubsequenceCte(queryBuilder, subsequencePrepCte);
    return buildRepeatedActionsCte(queryBuilder, subsequenceCte);
  }

  private static Cte buildTimeHorizonCte(Sequence sequence, QueryBuilder queryBuilder,
                                         Cte sequencesCte) {
    var columns = sequencesCte.select().select().columnNames().stream()
        .map(sequencesCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    Cte cte = new Cte("sequence_time_horizon", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(sequencesCte)
            .andQualify(new BooleanExpression(new TemplatedExpression(
                "{ts} - {window} <= {horizon}",
                TemplateDict.noMissing(Map.of(
                    "ts", new ToUnixTimestamp(
                        sequencesCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
                    "window", new ToUnixTimestamp(new WindowFunctionExpression(
                        new FunctionExpression("MIN",
                            sequencesCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
                        new ExpressionList(List.of(
                            sequencesCte.column(DomainCteBuilder.USER_ID_COLUMN),
                            sequencesCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)), ", "),
                        new ExpressionList(),
                        Optional.empty()
                    )),
                    "horizon", Constant.ofInt(sequence.getTimeHorizon().toSeconds())
                ))))));
    queryBuilder.with(cte);
    return cte;
  }

  private static Cte buildSubsequencePrepCte(QueryBuilder queryBuilder, Cte timehorizonCte) {
    var columns = timehorizonCte.select().select().columnNames().stream()
        .map(timehorizonCte::column)
        .collect(Collectors.toCollection(ArrayList::new));
    columns.add(new IfExpression(new TemplatedExpression(
        "{sequence} = 0 OR {action} = {window}", TemplateDict.noMissing(Map.of(
        "sequence", timehorizonCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
        "action", timehorizonCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
        "window", new WindowFunctionExpression(
            new FunctionExpression("LAG",
                timehorizonCte.column(DomainCteBuilder.STEP_NAME_COLUMN)),
            new ExpressionList(List.of(
                timehorizonCte.column(DomainCteBuilder.USER_ID_COLUMN),
                timehorizonCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)),
                ", "),
            new ExpressionList(timehorizonCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
            Optional.empty()
        )
    ))), Constant.ofInt(0), Constant.ofInt(1)).withAlias(NEW_SUBSEQUENCE_COLUMN));

    Cte cte = new Cte("sequence_sequences_prep", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(timehorizonCte)
    );
    queryBuilder.with(cte);
    return cte;
  }

  private static Cte buildSubsequenceCte(QueryBuilder queryBuilder, Cte subsequencePrepCte) {

    var columns = subsequencePrepCte.select().select().columnNames().stream()
        .filter(c -> !c.equals(NEW_SUBSEQUENCE_COLUMN))
        .map(subsequencePrepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));
    columns.add(new WindowFunctionExpression(new FunctionExpression("SUM",
        subsequencePrepCte.column(NEW_SUBSEQUENCE_COLUMN)),
        new ExpressionList(List.of(
            subsequencePrepCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequencePrepCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)),
            ", "),
        new ExpressionList(subsequencePrepCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
        Optional.empty()
    ).withAlias(SUBSEQUENCE_COLUMN));

    Cte cte = new Cte("sequence_sequences", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(subsequencePrepCte)
    );
    queryBuilder.with(cte);
    return cte;
  }

  private static Cte buildRepeatedActionsCte(QueryBuilder queryBuilder, Cte subsequenceCte) {
    var columns = subsequenceCte.select().select().columnNames().stream()
        .map(subsequenceCte::column)
        .collect(Collectors.toCollection(ArrayList::new));
    columns.add(new WindowFunctionExpression(
        new FunctionExpression("COUNT",
            new TemplatedExpression("*", TemplateDict.empty())),
        new ExpressionList(List.of(
            subsequenceCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequenceCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
            subsequenceCte.column(SUBSEQUENCE_COLUMN)),
            ", "),
        new ExpressionList(),
        Optional.empty()
    ).withAlias(REPEATITIONS_COLUMN));
    columns.add(new WindowFunctionExpression(
        new FunctionExpression("ROW_NUMBER"),
        new ExpressionList(List.of(
            subsequenceCte.column(DomainCteBuilder.USER_ID_COLUMN),
            subsequenceCte.column(SequencesCteBuilder.SEQUENCE_COLUMN),
            subsequenceCte.column(SUBSEQUENCE_COLUMN)),
            ", "),
        new ExpressionList(subsequenceCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
        Optional.empty()
    ).withAlias(REPEATITION_COLUMN));

    Cte cte = new Cte("sequence_repeated_actions", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(subsequenceCte)
    );
    queryBuilder.with(cte);
    return cte;
  }
}
