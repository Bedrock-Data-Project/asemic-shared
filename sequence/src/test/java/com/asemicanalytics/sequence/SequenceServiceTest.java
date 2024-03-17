package com.asemicanalytics.sequence;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.SqlQueryExecutor;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.databasetests.DatabaseHelper;
import com.asemicanalytics.sequence.sequence.StepTable;
import com.asemicanalytics.sql.h2.H2QueryExecutor;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SequenceServiceTest {
  private final SqlQueryExecutor executor = new H2QueryExecutor(
      DatabaseHelper.USER, DatabaseHelper.PASSWORD, DatabaseHelper.JDBC_URL, 10);
  private SequenceService sequenceService = new SequenceService(executor);

  @BeforeEach
  void setUp() throws SQLException {
    DatabaseHelper.dropAllTables();

  }

  private StepTable stepTable(String stepName) {
    return new StepTable(stepName, TableReference.of(stepName), List.of(),
        "user_id", "date_", "ts");
  }

  @Test
  void testSimple() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), """
         SELECT 1 as "user_id", PARSEDATETIME('2021-01-01 02:00:01', 'yyyy-MM-dd HH:mm:ss') as "ts", PARSEDATETIME('2021-01-01', 'yyyy-MM-dd') as "date_"
         UNION ALL SELECT 2, PARSEDATETIME('2021-01-01 02:00:01', 'yyyy-MM-dd HH:mm:ss'), PARSEDATETIME('2021-01-01', 'yyyy-MM-dd')
        """);

    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2022, 1, 5).atStartOfDay(ZoneId.of("UTC"))),
        "login", Map.of("login", stepTable("login")),
        TableReference.of("sequence_output"));

    var result = executor.submit("SELECT * FROM \"sequence_output\"", List.of(
        DataType.INTEGER, DataType.INTEGER, DataType.STRING, DataType.BOOLEAN, DataType.BOOLEAN
    ), false).get();

    System.out.println(result);
  }

}
