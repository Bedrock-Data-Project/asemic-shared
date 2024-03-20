package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.StepTable;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import com.asemicanalytics.sql.sql.builder.tablelike.Table;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DomainCteBuilder {
  public static String USER_ID_COLUMN = "user_id";
  public static String STEP_TS_COLUMN = "step_ts";
  public static String STEP_DATE_COLUMN = "step_date";
  public static String STEP_NAME_COLUMN = "step_name";

  public static Cte buildCte(Sequence sequence, QueryBuilder queryBuilder) {

    SelectStatement selectStatement = null;
    for (String domainAction : sequence.getDomainActions()) {
      var domainStatement = buildSingleStepSourceStatement(sequence, domainAction,
          sequence.getStepTable(domainAction));
      if (selectStatement == null) {
        selectStatement = domainStatement;
      } else {
        selectStatement.unionAll(domainStatement);
      }
    }
    Cte source = new Cte("sequence_source", queryBuilder.nextCteIndex(), selectStatement);
    queryBuilder.with(source);

    // Entrypoint to add externat joins, filters, etc

    var columns = source.select().select().columnNames().stream()
        .map(source::column)
        .collect(Collectors.toCollection(ArrayList::new));
    Cte unified = new Cte("sequence_unified", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(source));
    queryBuilder.with(unified);

    return unified;
  }

  private static SelectStatement buildSingleStepSourceStatement(Sequence sequence, String stepName,
                                                                StepTable stepTable) {
    var table = new Table(stepTable.tableReference());
    return new SelectStatement()
        .select(
            table.column(stepTable.userIdColumn()).withAlias(USER_ID_COLUMN),
            table.column(stepTable.timestampColumn()).withAlias(STEP_TS_COLUMN),
            // TODO convert to epoch seconds or milliseconds
            table.column(stepTable.dateColumn()).withAlias(STEP_DATE_COLUMN),
            new Constant(stepName, DataType.STRING).withAlias(STEP_NAME_COLUMN)
        )
        .from(table)
        .and(BooleanExpression.fromDateInterval(
            table.column(stepTable.dateColumn()), sequence.getDatetimeInterval()));
  }
}
