package com.asemicanalytics.sequence;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.querybuilder.DomainCteBuilder;
import com.asemicanalytics.sequence.querybuilder.SequencesCteBuilder;
import com.asemicanalytics.sequence.querybuilder.SqlQueryBuilder;
import com.asemicanalytics.sequence.querybuilder.SubsequencesCteBuilder;
import com.asemicanalytics.sequence.querylanguage.QueryLanguageEvaluator;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.StepTable;
import com.asemicanalytics.sql.sql.builder.ExpressionList;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import java.util.Map;

public class SequenceService {
  private final SqlQueryExecutor sqlQueryExecutor;

  public SequenceService(SqlQueryExecutor sqlQueryExecutor) {
    this.sqlQueryExecutor = sqlQueryExecutor;
  }

  public static Sequence parseSequence(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, StepTable> stepRepository) {
    QueryLanguageEvaluator queryLanguageEvaluator = new QueryLanguageEvaluator(stepRepository);
    return queryLanguageEvaluator.parse(datetimeInterval, sequenceQuery);
  }

  public String getSequenceSql(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, StepTable> stepRepository) {
    var query = SqlQueryBuilder.prepareCtes(
        parseSequence(datetimeInterval, sequenceQuery, stepRepository));
    query.queryBuilder().select(new SelectStatement()
        .selectStar()
        .from(query.steps())
        .orderBy(new ExpressionList(
            query.steps().column(DomainCteBuilder.USER_ID_COLUMN),
            query.steps().column(SequencesCteBuilder.SEQUENCE_COLUMN),
            query.steps().column(SubsequencesCteBuilder.SUBSEQUENCE_COLUMN),
            query.steps().column(DomainCteBuilder.STEP_TS_COLUMN)
        ))
    );
    return query.queryBuilder().render(sqlQueryExecutor.getDialect());
  }

  public void dumpSequenceToTable(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, StepTable> stepRepository,
      TableReference tableReference) {
    var select = getSequenceSql(datetimeInterval, sequenceQuery, stepRepository);
    var createTable =
        sqlQueryExecutor.getDialect().createTableFromSelect(select, tableReference, true);
    System.out.println(createTable); // TODO remove
    sqlQueryExecutor.executeDdl(createTable);
  }

}
