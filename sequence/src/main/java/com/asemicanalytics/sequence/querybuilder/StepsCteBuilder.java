package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.Step;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.expression.ExpressionList;
import com.asemicanalytics.sql.sql.builder.expression.FunctionExpression;
import com.asemicanalytics.sql.sql.builder.expression.IfExpression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.expression.casecondition.CaseExpression;
import com.asemicanalytics.sql.sql.builder.expression.casecondition.CaseWhenThen;
import com.asemicanalytics.sql.sql.builder.expression.windowfunction.WindowFunctionExpression;
import com.asemicanalytics.sql.sql.builder.select.SelectStatement;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StepsCteBuilder {
  public static final String IS_VALID_COLUMN = "is_valid";
  public static final String STEP_COLUMN = "step";

  public static Cte buildCte(Sequence sequence, QueryBuilder queryBuilder, Cte subsequencesCte) {
    List<List<Step>> partitionedSteps = partitionSteps(sequence.getSteps());
    Cte lastStepCte = subsequencesCte;

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

  private static Cte buildNonOptionalStepCte(QueryBuilder queryBuilder,
                                             Cte lastStepCte, List<Step> steps) {
    var columns = lastStepCte.select().select().columnNames().stream()
        .map(lastStepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    // TODO mislim da else false treba samo ako je ovo poslednji cte
    // ako nije, onda else null
    columns.add(new CaseExpression(
        lastStepCte.column(SubsequencesCteBuilder.SUBSEQUENCE_COLUMN),
        steps.stream()
            .map(s -> new CaseWhenThen(Constant.ofInt(s.getIndex()),
                new TemplatedExpression("{step} in ({step_actions})",
                    TemplateDict.noMissing(Map.of(
                        "step", lastStepCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                        "step_actions", new ExpressionList(s.getStepNames().stream()
                            .map(Constant::ofString)
                            .collect(Collectors.toList()), ", ")))
                )))
            .collect(Collectors.toList()),
        new Constant("FALSE", DataType.BOOLEAN)
    ).withAlias(IS_VALID_COLUMN));

    columns.add(new CaseExpression(
        lastStepCte.column(SubsequencesCteBuilder.SUBSEQUENCE_COLUMN),
        steps.stream()
            .map(s -> new CaseWhenThen(Constant.ofInt(s.getIndex()),
                new IfExpression(new TemplatedExpression("{step} in ({step_actions})",
                    TemplateDict.noMissing(Map.of(
                        "step", lastStepCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                        "step_actions", new ExpressionList(s.getStepNames().stream()
                            .map(Constant::ofString)
                            .collect(Collectors.toList()), ", ")))
                ), Constant.ofInt(s.getIndex()), Constant.ofNull())))
            .collect(Collectors.toList())
    ).withAlias(STEP_COLUMN));

    Cte cte = new Cte("sequence_non_optional", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(lastStepCte)
    );
    queryBuilder.with(cte);
    return cte;

  }

  private static Cte buildCombinedStepsCte(QueryBuilder queryBuilder, Cte lastStepCte) {
    var columns = lastStepCte.select().select().columnNames().stream()
        .filter(c -> !c.equals(IS_VALID_COLUMN))
        .map(lastStepCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(new WindowFunctionExpression(new FunctionExpression("MIN",
        lastStepCte.column(IS_VALID_COLUMN)),
        new ExpressionList(List.of(
            lastStepCte.column(DomainCteBuilder.USER_ID_COLUMN),
            lastStepCte.column(SequencesCteBuilder.SEQUENCE_COLUMN)),
            ", "),
        new ExpressionList(lastStepCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
        Optional.empty()
    ).withAlias(IS_VALID_COLUMN));

    Cte cte = new Cte("sequence_steps_combined", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(lastStepCte)
    );
    queryBuilder.with(cte);
    return cte;
  }

}
