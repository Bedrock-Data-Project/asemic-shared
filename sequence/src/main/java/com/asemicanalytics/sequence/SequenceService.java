package com.asemicanalytics.sequence;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.querybuilder.SqlQueryBuilder;
import com.asemicanalytics.sequence.querylanguage.QueryLanguageEvaluator;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.StepTable;
import com.asemicanalytics.sql.sql.builder.SelectStatement;
import java.util.Map;

public class SequenceService {
  private final SqlQueryExecutor sqlQueryExecutor;

  public SequenceService(SqlQueryExecutor sqlQueryExecutor) {
    this.sqlQueryExecutor = sqlQueryExecutor;
  }

  public SqlQueryBuilder.SequenceQuery getSequenceQuery(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, StepTable> stepRepository) {
    QueryLanguageEvaluator queryLanguageEvaluator = new QueryLanguageEvaluator(stepRepository);
    Sequence sequence = queryLanguageEvaluator.parse(datetimeInterval, sequenceQuery);
    return SqlQueryBuilder.prepareCtes(sequence);
  }

  public String getSequenceSql(
      DatetimeInterval datetimeInterval, String sequenceQuery,
      Map<String, StepTable> stepRepository) {
    var query = getSequenceQuery(datetimeInterval, sequenceQuery, stepRepository);
    query.queryBuilder().select(new SelectStatement()
        .selectStar()
        .from(query.steps())
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
    sqlQueryExecutor.executeDdl(createTable);
  }

}
