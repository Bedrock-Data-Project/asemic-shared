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

class SequenceOfOneTest extends SequenceBaseTest {
  @Test
  void testIgnoreEventsOutOfDateInterval() throws SQLException, ExecutionException, InterruptedException {
    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
        new UserActionRow(1, Duration.ofHours(2).plusSeconds(1)),
        new UserActionRow(1, Duration.ofHours(2).plusSeconds(7)),
        new UserActionRow(1, Duration.ofDays(2).plusHours(2).plusSeconds(8)),
        new UserActionRow(1, Duration.ofDays(3).plusHours(2).plusSeconds(9))
    ));

    String sequenceQuery = "match login;";
    sequenceService.dumpSequenceToTable(new DatetimeInterval(
            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
        sequenceQuery, STEP_REPOSITORY,
        TableReference.of("sequence_output"));

    assertResult(List.of(
        new ResultRow(1, Duration.ofHours(2).plusSeconds(1), "login", 1, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofHours(2).plusSeconds(7), "login", 2, 1, 1, 1, 1, true),
        new ResultRow(1, Duration.ofDays(2).plusHours(2).plusSeconds(8), "login", 3, 1, 1, 1, 1,
            true)
    ));
  }

}
