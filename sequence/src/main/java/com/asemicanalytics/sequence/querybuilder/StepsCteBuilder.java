package com.asemicanalytics.sequence.querybuilder;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.boolean_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.case_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.cte;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.function;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.in;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.int_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.null_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.parse;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.window;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.Step;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.QueryFactory;
import com.asemicanalytics.sql.sql.builder.tokens.TableLike;
import com.asemicanalytics.sql.sql.builder.tokens.TemplateDict;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StepsCteBuilder {
  public static final String IS_VALID_COLUMN = "is_valid";
  public static final String STEP_COLUMN = "step";

  public static TableLike buildCte(Sequence sequence, QueryBuilder queryBuilder,
                                   TableLike subsequencesCte) {
    List<List<Step>> partitionedSteps = partitionSteps(sequence.getSteps());
    TableLike lastStepCte = subsequencesCte;

    for (List<Step> steps : partitionedSteps) {
      // TODO check if step is optional
      lastStepCte = buildNonOptionalStepCte(queryBuilder, lastStepCte, steps);
    }

    return buildCombinedStepsCte(queryBuilder, lastStepCte);
  }

  private static List<List<Step>> partitionSteps(List<Step> steps) {
    // TODO when we add support for optional steps, we need to split on optional step here
    return List.of(steps);
  }

  private static TableLike buildNonOptionalStepCte(QueryBuilder queryBuilder,
                                                   TableLike lastStepCte, List<Step> steps) {
    var columns = lastStepCte.columnNames().stream()
        .map(lastStepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    // TODO mislim da else false treba samo ako je ovo poslednji cte
    // ako nije, onda else null
    var isValidColumn = case_(lastStepCte.column(SubsequencesCteBuilder.SUBSEQUENCE_COLUMN));
    for (Step step : steps) {
      isValidColumn.when(int_(step.getIndex()),
          parse("{step} in ({step_actions})",
              TemplateDict.noMissing(Map.of(
                  "step", lastStepCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                  "step_actions", in(step.getStepNames().stream()
                      .map(QueryFactory::string_)
                      .collect(Collectors.toList()))))
          ));
    }
    isValidColumn.else_(boolean_(false));
    columns.add(isValidColumn.withAlias(IS_VALID_COLUMN));

    var stepColumn = case_(lastStepCte.column(SubsequencesCteBuilder.SUBSEQUENCE_COLUMN));
    for (Step step : steps) {
      stepColumn.when(int_(step.getIndex()),
          parse("{step} in ({step_actions})",
              TemplateDict.noMissing(Map.of(
                  "step", lastStepCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                  "step_actions", in(step.getStepNames().stream()
                      .map(QueryFactory::string_)
                      .collect(Collectors.toList()))))
          ).asCondition().if_(int_(step.getIndex()), null_()));
    }
    columns.add(stepColumn.withAlias(STEP_COLUMN));

    return cte(queryBuilder, "sequence_non_optional",
        select()
            .select(columns)
            .from(lastStepCte)
    );
  }

  private static TableLike buildCombinedStepsCte(QueryBuilder queryBuilder, TableLike lastStepCte) {
    var columns = lastStepCte.columnNames().stream()
        .filter(c -> !c.equals(IS_VALID_COLUMN))
        .map(lastStepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(
        window(function("MIN", lastStepCte.column(IS_VALID_COLUMN)))
            .partitionBy(
                lastStepCte.column(DomainCteBuilder.USER_ID_COLUMN),
                lastStepCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)
            )
            .orderBy(
                lastStepCte.column(DomainCteBuilder.STEP_TS_COLUMN)
            )
            .withAlias(IS_VALID_COLUMN));

    return cte(queryBuilder, "sequence_steps_combined",
        select()
            .select(columns)
            .from(lastStepCte)
    );
  }

}
