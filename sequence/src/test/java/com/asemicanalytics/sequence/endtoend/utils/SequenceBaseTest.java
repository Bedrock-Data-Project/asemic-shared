package com.asemicanalytics.sequence.endtoend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import brave.Tracing;
import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.SqlResultRow;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.column.Columns;
import com.asemicanalytics.core.logicaltable.EventLikeLogicalTable;
import com.asemicanalytics.core.logicaltable.TemporalLogicalTable;
import com.asemicanalytics.core.logicaltable.event.EventLogicalTable;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sql.sql.h2.H2QueryExecutor;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;

public class SequenceBaseTest {
  protected final SqlQueryExecutor executor = new H2QueryExecutor(
      DatabaseHelper.USER, DatabaseHelper.PASSWORD, DatabaseHelper.JDBC_URL, 10);
  protected final Map<String, EventLogicalTable> STEP_COLUMN_SOURCES = Map.of(
      "login", eventLogicalTable("login"),
      "battle", eventLogicalTable("battle"),
      "transaction", eventLogicalTable("transaction")
  );
  protected SequenceService sequenceService = new SequenceService(executor);

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseHelper.dropAllTables();
    Tracing.newBuilder().build();

  }

  private EventLogicalTable eventLogicalTable(String stepName) {
    return new EventLogicalTable(
        stepName, "", Optional.empty(), TableReference.of(stepName),
        new Columns<>(new LinkedHashMap<>(Map.of(
            "date_",
            Column.ofHidden("date_", DataType.DATE).withTag(TemporalLogicalTable.DATE_COLUMN_TAG),
            "ts", Column.ofHidden("ts", DataType.DATETIME)
                .withTag(EventLikeLogicalTable.TIMESTAMP_COLUMN_TAG),
            "user_id", Column.ofHidden("user_id", DataType.STRING)
                .withTag(EventLogicalTable.ENTITY_ID_COLUMN_TAG)
        ))),
        Map.of(), Optional.empty(), Set.of());
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
