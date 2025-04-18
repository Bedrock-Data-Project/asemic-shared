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

class DomainTest extends SequenceBaseTest {

  @Test
  void testInvalidEventsTaggedCorrectly()
      throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(3)),
        new UserActionRow(1, Duration.ofSeconds(7))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(4)),
        new UserActionRow(1, Duration.ofSeconds(8))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(5)),
        new UserActionRow(1, Duration.ofSeconds(6)),
        new UserActionRow(1, Duration.ofSeconds(9)),
        new UserActionRow(1, Duration.ofSeconds(10)),

        new UserActionRow(2, Duration.ofSeconds(1)),
        new UserActionRow(2, Duration.ofSeconds(2))
    ));

    String sequenceQuery = "domain transaction; match login >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "transaction", 0, 0, 2, 1, null, false),
        new ResultRow(1, Duration.ofSeconds(2), "transaction", 0, 0, 2, 2, null, false),
        new ResultRow(1, Duration.ofSeconds(3), "login", 1, 1, 1, 1, 1L, true),
        new ResultRow(1, Duration.ofSeconds(4), "battle", 1, 2, 1, 1, 2L, true),
        new ResultRow(1, Duration.ofSeconds(5), "transaction", 1, 3, 2, 1, null, false),
        new ResultRow(1, Duration.ofSeconds(6), "transaction", 1, 3, 2, 2, null, false),
        new ResultRow(1, Duration.ofSeconds(7), "login", 2, 1, 1, 1, 1L, true),
        new ResultRow(1, Duration.ofSeconds(8), "battle", 2, 2, 1, 1, 2L, true),
        new ResultRow(1, Duration.ofSeconds(9), "transaction", 2, 3, 2, 1, null, false),
        new ResultRow(1, Duration.ofSeconds(10), "transaction", 2, 3, 2, 2, null, false),

        new ResultRow(2, Duration.ofSeconds(1), "transaction", 0, 0, 2, 1, null, false),
        new ResultRow(2, Duration.ofSeconds(2), "transaction", 0, 0, 2, 2, null, false)
    ));
  }

  @Test
  void testDomainFilter()
      throws SQLException, ExecutionException, InterruptedException {

    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(2, Duration.ofSeconds(5)),
        new UserActionRow(3, Duration.ofSeconds(6)),
        new UserActionRow(4, Duration.ofSeconds(9)),
        new UserActionRow(5, Duration.ofSeconds(10))
    ));

    String sequenceQuery = "domain transaction where user_id > 1; match transaction;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(2, Duration.ofSeconds(5), "transaction", 1, 1, 1, 1, 1L, true),
        new ResultRow(3, Duration.ofSeconds(6), "transaction", 1, 1, 1, 1, 1L, true),
        new ResultRow(4, Duration.ofSeconds(9), "transaction", 1, 1, 1, 1, 1L, true),
        new ResultRow(5, Duration.ofSeconds(10), "transaction", 1, 1, 1, 1, 1L, true)
    ));
  }

  @Test
  void testDomainAliases()
      throws SQLException, ExecutionException, InterruptedException {

    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(2, Duration.ofSeconds(5)),
        new UserActionRow(3, Duration.ofSeconds(6)),
        new UserActionRow(4, Duration.ofSeconds(9)),
        new UserActionRow(5, Duration.ofSeconds(10))
    ));

    String sequenceQuery = "domain transaction as t_alias; match t_alias;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "t_alias", 1, 1, 1, 1, 1L, true),
        new ResultRow(1, Duration.ofSeconds(2), "t_alias", 2, 1, 1, 1, 1L, true),
        new ResultRow(2, Duration.ofSeconds(5), "t_alias", 1, 1, 1, 1, 1L, true),
        new ResultRow(3, Duration.ofSeconds(6), "t_alias", 1, 1, 1, 1, 1L, true),
        new ResultRow(4, Duration.ofSeconds(9), "t_alias", 1, 1, 1, 1, 1L, true),
        new ResultRow(5, Duration.ofSeconds(10), "t_alias", 1, 1, 1, 1, 1L, true)
    ));
  }
}
