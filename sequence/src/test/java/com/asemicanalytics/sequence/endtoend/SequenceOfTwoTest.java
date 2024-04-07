package com.asemicanalytics.sequence.endtoend;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.endtoend.utils.DatabaseHelper;
import com.asemicanalytics.sequence.endtoend.utils.ResultRow;
import com.asemicanalytics.sequence.endtoend.utils.SequenceBaseTest;
import com.asemicanalytics.sequence.endtoend.utils.UserActionRow;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class SequenceOfTwoTest extends SequenceBaseTest {
  @Test
  void testTwoValidSequences() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(11))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(12))
    ));

    String sequenceQuery = "match login >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
        new ResultRow(1, Duration.ofSeconds(11), "login", 2, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(12), "battle", 2, 2, 1, 1, 2, true)
    ));
  }

  @Test
  void testTwoValidSequencesWithRepeatingSteps()
      throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(11))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(3)),
        new UserActionRow(1, Duration.ofSeconds(4)),
        new UserActionRow(1, Duration.ofSeconds(12)),
        new UserActionRow(1, Duration.ofSeconds(13))
    ));

    String sequenceQuery = "match login >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 3, 1, 2, true),
        new ResultRow(1, Duration.ofSeconds(3), "battle", 1, 2, 3, 2, 2, true),
        new ResultRow(1, Duration.ofSeconds(4), "battle", 1, 2, 3, 3, 2, true),

        new ResultRow(1, Duration.ofSeconds(11), "login", 2, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(12), "battle", 2, 2, 2, 1, 2, true),
        new ResultRow(1, Duration.ofSeconds(13), "battle", 2, 2, 2, 2, 2, true)
    ));
  }

  @Test
  void testPartialSequences() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(11)),
        new UserActionRow(2, Duration.ofSeconds(11))

    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofDays(10))
    ));

    String sequenceQuery = "match login >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(11), "login", 2, 1, 1, 1, 1, true),
        new ResultRow(2, Duration.ofSeconds(11), "login", 1, 1, 1, 1, 1, true)
    ));
  }

  @Test
  void testInvalidSequences() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(3))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(5))
    ));

    String sequenceQuery = "match login >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "battle", 0, 0, 2, 1, 0, false),
        new ResultRow(1, Duration.ofSeconds(2), "battle", 0, 0, 2, 2, 0, false),
        new ResultRow(1, Duration.ofSeconds(3), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(5), "battle", 1, 2, 1, 1, 2, true)
    ));
  }

}
