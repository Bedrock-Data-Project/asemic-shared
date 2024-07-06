package com.asemicanalytics.sequence;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.logicaltable.action.ActionLogicalTable;
import com.asemicanalytics.sequence.querybuilder.DomainCteBuilder;
import com.asemicanalytics.sequence.querybuilder.SequenceQuery;
import com.asemicanalytics.sequence.querybuilder.SqlQueryBuilder;
import com.asemicanalytics.sequence.querylanguage.QueryLanguageEvaluator;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import java.util.List;
import java.util.Map;

public class SequenceService {
  private final SqlQueryExecutor sqlQueryExecutor;

  public SequenceService(SqlQueryExecutor sqlQueryExecutor) {
    this.sqlQueryExecutor = sqlQueryExecutor;
  }

  public static Sequence parseSequence(String sequenceQuery,
                                       Map<String, ActionLogicalTable> stepTables) {
    QueryLanguageEvaluator queryLanguageEvaluator = new QueryLanguageEvaluator(stepTables);
    return queryLanguageEvaluator.parse(sequenceQuery);
  }

  public SequenceQuery getSequenceQuery(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, ActionLogicalTable> stepTables, List<String> includeColumns) {
    return SqlQueryBuilder.prepareCtes(
        parseSequence(sequenceQuery, stepTables), datetimeInterval, includeColumns);

  }

  public String getSequenceSql(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, ActionLogicalTable> stepTables, List<String> includeColumns) {
    var query =
        getSequenceQuery(datetimeInterval, sequenceQuery, stepTables, includeColumns);
    query.queryBuilder().select(new SelectStatement()
        .selectStar()
        .from(query.steps())
        .orderBy(new ExpressionList(
            query.steps().column(DomainCteBuilder.USER_ID_COLUMN),
            query.steps().column(DomainCteBuilder.STEP_TS_COLUMN)
        ))
    );
    return query.queryBuilder().render(sqlQueryExecutor.getDialect());
  }

  public void dumpSequenceToTable(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, ActionLogicalTable> stepTables,
      TableReference tableReference, List<String> includeColumns) {
    var select = getSequenceSql(datetimeInterval, sequenceQuery, stepTables, includeColumns);
    var createTable =
        sqlQueryExecutor.getDialect().createTableFromSelect(select, tableReference, true);
    sqlQueryExecutor.executeDdl(createTable);
  }

}
