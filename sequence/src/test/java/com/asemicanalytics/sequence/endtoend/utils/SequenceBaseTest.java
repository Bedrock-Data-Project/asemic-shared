package com.asemicanalytics.sequence.endtoend.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.SqlResult;
import com.asemicanalytics.core.SqlResultRow;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.datasource.UserActionDatasource;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sql.h2.H2QueryExecutor;
import com.asemicanalytics.sql.sql.builder.tablelike.Table;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import com.asemicanalytics.sql.sql.columnsource.TableColumnSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;

public class SequenceBaseTest {
  protected final SqlQueryExecutor executor = new H2QueryExecutor(
      DatabaseHelper.USER, DatabaseHelper.PASSWORD, DatabaseHelper.JDBC_URL, 10);
  protected final Map<String, ColumnSource> STEP_COLUMN_SOURCES = Map.of(
      "login", columnSource("login"),
      "battle", columnSource("battle"),
      "transaction", columnSource("transaction")
  );
  protected SequenceService sequenceService = new SequenceService(executor);

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseHelper.dropAllTables();

  }

  private ColumnSource columnSource(String stepName) {
    return new TableColumnSource(new UserActionDatasource(
        stepName, "", Optional.empty(), TableReference.of(stepName),
        new LinkedHashMap<>(Map.of(
            "date_", Column.ofHidden("date_", DataType.DATE),
            "ts", Column.ofHidden("ts", DataType.DATETIME),
            "user_id", Column.ofHidden("user_id", DataType.STRING)
        )),
        Map.of(), TimeGrains.min15,
        "date_", "ts", "user_id"),
        new Table(TableReference.of(stepName)));
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
