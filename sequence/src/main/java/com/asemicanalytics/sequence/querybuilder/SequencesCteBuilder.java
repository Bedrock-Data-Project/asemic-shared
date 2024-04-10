package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.expression.FunctionExpression;
import com.asemicanalytics.sql.sql.builder.expression.IfExpression;
import com.asemicanalytics.sql.sql.builder.expression.TemplateDict;
import com.asemicanalytics.sql.sql.builder.expression.TemplatedExpression;
import com.asemicanalytics.sql.sql.builder.expression.windowfunction.WindowFunctionExpression;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SequencesCteBuilder {
  public static final String SEQUENCE_COLUMN = "sequence";

  public static Cte buildCte(Sequence sequence, QueryBuilder queryBuilder, Cte domainCte) {
    if (sequence.isStartStepRepeated()) {
      throw new UnsupportedOperationException();
    } else {
      return buildIfStartStepNotRepeated(sequence, queryBuilder, domainCte);
    }
  }

  private static Cte buildIfStartStepNotRepeated(Sequence sequence, QueryBuilder queryBuilder,
                                                 Cte domainCte) {
    var stepValues = new ExpressionList(sequence.getSteps().get(0).getStepNames().stream()
        .map(Constant::ofString)
        .collect(Collectors.toList()), ", ");

    var windowAggregation = new FunctionExpression("SUM", new IfExpression(
        new TemplatedExpression("{step_column} = {step_value}",
            TemplateDict.noMissing(Map.of(
                "step_column", domainCte.column(DomainCteBuilder.STEP_NAME_COLUMN),
                "step_value", stepValues
            ))), Constant.ofInt(1), Constant.ofInt(0)));

    var columns = domainCte.select().select().columnNames().stream()
        .map(domainCte::column)
        .collect(Collectors.toCollection(ArrayList::new));

    columns.add(new WindowFunctionExpression(
        windowAggregation,
        new ExpressionList(domainCte.column(DomainCteBuilder.USER_ID_COLUMN)),
        new ExpressionList(domainCte.column(DomainCteBuilder.STEP_TS_COLUMN)),
        Optional.empty()
    ).withAlias(SEQUENCE_COLUMN));

    Cte sequences = new Cte("sequence_sequences", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(domainCte)
    );
    queryBuilder.with(sequences);
    return sequences;
  }
}
