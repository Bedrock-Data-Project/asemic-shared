package com.asemicanalytics.sequence.querybuilder;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.cte;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.null_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.string_;
import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.table;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.tokens.Cte;
import com.asemicanalytics.sql.sql.builder.tokens.Expression;
import com.asemicanalytics.sql.sql.builder.tokens.QueryBuilder;
import com.asemicanalytics.sql.sql.builder.tokens.SelectStatement;
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
    var source = cte(queryBuilder, "sequence_source", selectStatement);

    var columns = source.columnNames().stream()
        .map(source::column)
        .collect(Collectors.toCollection(ArrayList::new));
    includeColumns.forEach(column -> columns.add(source.column(column)));

    return cte(queryBuilder, "sequence_unified",
        select()
            .select(columns)
            .from(source));
  }

  private static SelectStatement buildSingleStepSourceStatement(
      Sequence sequence, DomainStep domainStep,
      DatetimeInterval datetimeInterval, List<String> includeColumns) {

    ActionLogicalTable stepLogicalTable = sequence.getTable(domainStep.actionLogicalTableName());
    var table = table(stepLogicalTable.getTable());
    var columns = new ArrayList<Expression>();
    columns.add(table.column(stepLogicalTable.entityIdColumn().getId()).withAlias(USER_ID_COLUMN));
    columns.add(
        table.column(stepLogicalTable.getTimestampColumn().getId()).withAlias(STEP_TS_COLUMN));
    columns.add(table.column(stepLogicalTable.getDateColumn().getId()).withAlias(STEP_DATE_COLUMN));
    columns.add(string_(domainStep.name()).withAlias(STEP_NAME_COLUMN));

    for (String column : includeColumns) {
      if (!sequence.getSteps().get(0).getStepNames().contains(domainStep.name())) {
        columns.add(null_().withAlias(column));
      }
    }

    var statement = select()
        .select(columns)
        .from(table)
        .and(table.column(stepLogicalTable.getDateColumn().getId()).between(datetimeInterval));
    domainStep.filter().ifPresent(statement::and);
    return statement;
  }
}
