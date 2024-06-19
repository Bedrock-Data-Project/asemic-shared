package com.asemicanalytics.sequence.querybuilder;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import com.asemicanalytics.sql.sql.builder.booleanexpression.BooleanExpression;
import com.asemicanalytics.sql.sql.builder.expression.Constant;
import com.asemicanalytics.sql.sql.builder.tablelike.Cte;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomainCteBuilder {
  public static String USER_ID_COLUMN = "user_id";
  public static String STEP_TS_COLUMN = "step_ts";
  public static String STEP_DATE_COLUMN = "step_date";
  public static String STEP_NAME_COLUMN = "step_name";

  public static Cte buildCte(Sequence sequence, QueryBuilder queryBuilder,
                             DatetimeInterval datetimeInterval, List<String> includeColumns) {

    SelectStatement selectStatement = null;
    for (var domainAction : sequence.getDomainActions()) {
      var domainStatement = buildSingleStepSourceStatement(
          sequence, domainAction, datetimeInterval, includeColumns);
      if (selectStatement == null) {
        selectStatement = domainStatement;
      } else {
        selectStatement.unionAll(domainStatement);
      }
    }
    Cte source = new Cte("sequence_source", queryBuilder.nextCteIndex(), selectStatement);
    queryBuilder.with(source);

    var columns = source.select().select().columnNames().stream()
        .map(source::column)
        .collect(Collectors.toCollection(ArrayList::new));
    includeColumns.forEach(column -> columns.add(source.column(column)));

    Cte unified = new Cte("sequence_unified", queryBuilder.nextCteIndex(),
        new SelectStatement()
            .select(new ExpressionList(columns))
            .from(source));
    queryBuilder.with(unified);

    return unified;
  }

  private static SelectStatement buildSingleStepSourceStatement(
      Sequence sequence, DomainStep domainStep,
      DatetimeInterval datetimeInterval, List<String> includeColumns) {

    ColumnSource stepColumnSource = sequence.getStepColumnSource(domainStep.columnSourceName());
    ActionLogicalTable stepLogicalTable = (ActionLogicalTable) stepColumnSource.getLogicalTable();
    var columns = new ExpressionList(
        stepColumnSource.loadColumn(stepLogicalTable.entityIdColumn().getId(),
            datetimeInterval).withAlias(USER_ID_COLUMN),
        stepColumnSource.loadColumn(stepLogicalTable.getTimestampColumn().getId(),
            datetimeInterval).withAlias(STEP_TS_COLUMN),
        stepColumnSource.loadColumn(stepLogicalTable.getDateColumn().getId(),
            datetimeInterval).withAlias(STEP_DATE_COLUMN),
        new Constant(domainStep.name(), DataType.STRING).withAlias(STEP_NAME_COLUMN)
    );

    for (String column : includeColumns) {
      if (!sequence.getSteps().get(0).getStepNames().contains(domainStep.name())) {
        columns.add(Constant.ofNull().withAlias(column));
      }
    }

    var statement = new SelectStatement()
        .select(columns)
        .from(stepColumnSource.table())
        .and(BooleanExpression.fromDateInterval(
            stepColumnSource.loadColumn(stepLogicalTable.getDateColumn().getId(),
                datetimeInterval), datetimeInterval));
    domainStep.filter().ifPresent(statement::and);
    return statement;
  }
}
