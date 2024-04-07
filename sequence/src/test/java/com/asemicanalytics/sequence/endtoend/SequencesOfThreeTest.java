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

class SequencesOfThreeTest extends SequenceBaseTest {
  @Test
  void tetSecondStepAppearsTwice() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofSeconds(1)),
        new UserActionRow(2, Duration.ofSeconds(1))

    ));
    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
        new UserActionRow(1, Duration.ofSeconds(2)),
        new UserActionRow(1, Duration.ofSeconds(3)),
        new UserActionRow(1, Duration.ofSeconds(5))
    ));
    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
        new UserActionRow(1, Duration.ofSeconds(4)),
        new UserActionRow(2, Duration.ofSeconds(2))
    ));

    String sequenceQuery = "match login >> battle >> transaction >> battle;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_COLUMN_SOURCES,
        TableReference.of("sequence_output"), List.of());

    assertResult(List.of(
        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 2, 1, 2, true),
        new ResultRow(1, Duration.ofSeconds(3), "battle", 1, 2, 2, 2, 2, true),
        new ResultRow(1, Duration.ofSeconds(4), "transaction", 1, 3, 1, 1, 3, true),
        new ResultRow(1, Duration.ofSeconds(5), "battle", 1, 4, 1, 1, 4, true),

        new ResultRow(2, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(2, Duration.ofSeconds(2), "transaction", 1, 2, 1, 1, 0, false)
    ));
  }
}
