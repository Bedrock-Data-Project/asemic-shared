package com.asemicanalytics.sequence;

import static com.asemicanalytics.sql.sql.builder.tokens.QueryFactory.select;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTables;
import com.asemicanalytics.sequence.querybuilder.DomainCteBuilder;
import com.asemicanalytics.sequence.querybuilder.SequenceQuery;
import com.asemicanalytics.sequence.querybuilder.SqlQueryBuilder;
import com.asemicanalytics.sequence.querylanguage.QueryLanguageEvaluator;
import com.asemicanalytics.sequence.sequence.Sequence;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SequenceService {
  private final SqlQueryExecutor sqlQueryExecutor;

  public SequenceService(SqlQueryExecutor sqlQueryExecutor) {
    this.sqlQueryExecutor = sqlQueryExecutor;
  }

  public static Sequence parseSequence(String sequenceQuery,
                                       EventLogicalTables stepTables) {
    QueryLanguageEvaluator queryLanguageEvaluator = new QueryLanguageEvaluator(stepTables);
    return queryLanguageEvaluator.parse(sequenceQuery);
  }

  public SequenceQuery getSequenceQuery(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      EventLogicalTables stepTables, List<String> includeColumns) {
    return SqlQueryBuilder.prepareCtes(
        parseSequence(sequenceQuery, stepTables), datetimeInterval, includeColumns);

  }

  public String getSequenceSql(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      EventLogicalTables stepTables, List<String> includeColumns) {
    var query =
        getSequenceQuery(datetimeInterval, sequenceQuery, stepTables, includeColumns);
    query.queryBuilder().select(select()
        .selectStar()
        .from(query.steps())
        .orderBy(
            query.steps().column(DomainCteBuilder.USER_ID_COLUMN),
            query.steps().column(DomainCteBuilder.STEP_TS_COLUMN)
        )
    );
    return query.queryBuilder().render(sqlQueryExecutor.getDialect());
  }

  public void dumpSequenceToTable(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      EventLogicalTables stepTables,
      TableReference tableReference, List<String> includeColumns)
      throws ExecutionException, InterruptedException {
    var select = getSequenceSql(datetimeInterval, sequenceQuery, stepTables, includeColumns);
    var createTable =
        sqlQueryExecutor.getDialect().createTableFromSelect(select, tableReference, true);
    sqlQueryExecutor.submitExecuteDdl(createTable).get();
  }

}
