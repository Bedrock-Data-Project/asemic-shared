package com.asemicanalytics.sequence.endtoend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.SqlResultRow;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.StepTable;
import com.asemicanalytics.sql.h2.H2QueryExecutor;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;

public class SequenceBaseTest {
  protected final SqlQueryExecutor executor = new H2QueryExecutor(
      DatabaseHelper.USER, DatabaseHelper.PASSWORD, DatabaseHelper.JDBC_URL, 10);
  protected final Map<String, StepTable> STEP_REPOSITORY = Map.of(
      "login", stepTable("login"),
      "battle", stepTable("battle"),
      "transaction", stepTable("transaction")
  );
  protected SequenceService sequenceService = new SequenceService(executor);

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseHelper.dropAllTables();

  }

  protected StepTable stepTable(String stepName) {
    return new StepTable(stepName, TableReference.of(stepName), List.of(),
        "user_id", "date_", "ts");
  }

  private SqlResult result() throws ExecutionException, InterruptedException {
    return executor.submit("SELECT * FROM \"sequence_output\"", List.of(
        DataType.INTEGER, DataType.DATETIME, DataType.DATE, DataType.STRING,
        DataType.INTEGER, DataType.INTEGER, DataType.INTEGER, DataType.INTEGER, DataType.INTEGER,
        DataType.BOOLEAN
    ), false).get();
  }

  protected void assertResult(List<ResultRow> rows)
      throws ExecutionException, InterruptedException {
    var sqlRows = rows.stream().map(ResultRow::toSqlResultRow).toList();
    String expected = sqlRows.stream()
        .map(SqlResultRow::toString)
        .collect(Collectors.joining("\n"));
    String actual = result().rows().stream()
        .map(SqlResultRow::toString)
        .collect(Collectors.joining("\n"));
    assertEquals(expected, actual);
  }

}
