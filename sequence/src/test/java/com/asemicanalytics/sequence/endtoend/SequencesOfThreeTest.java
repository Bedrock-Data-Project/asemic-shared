package com.asemicanalytics.sequence.endtoend;

import com.asemicanalytics.sequence.endtoend.utils.SequenceBaseTest;

class SequencesOfThreeTest extends SequenceBaseTest {
//  @Test
//  void testMiddleRepeated() throws SQLException, ExecutionException, InterruptedException {
//    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(1)),
//        new UserActionRow(2, Duration.ofSeconds(1))
//    ));
//    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(2)),
//        new UserActionRow(1, Duration.ofSeconds(5)),
//        new UserActionRow(2, Duration.ofSeconds(2)),
//        new UserActionRow(2, Duration.ofSeconds(3)),
//        new UserActionRow(2, Duration.ofSeconds(4))
//    ));
//    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(3)),
//        new UserActionRow(1, Duration.ofSeconds(4)),
//        new UserActionRow(2, Duration.ofSeconds(5)),
//        new UserActionRow(2, Duration.ofSeconds(6))
//    ));
//
//    String sequenceQuery = "login >> battle >> battle >> transaction";
//    sequenceService.dumpSequenceToTable(new DatetimeInterval(
//            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
//            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
//        sequenceQuery, STEP_REPOSITORY,
//        TableReference.of("sequence_output"));
//
//    assertResult(List.of(
//        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
//        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
//        new ResultRow(1, Duration.ofSeconds(3), "transaction", 1, 3, 2, 1, 3, false),
//        new ResultRow(1, Duration.ofSeconds(4), "transaction", 1, 3, 2, 2, 3, false),
//        new ResultRow(1, Duration.ofSeconds(5), "battle", 1, 4, 1, 1, 4, false),
//
//        new ResultRow(2, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
//        new ResultRow(2, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
//        new ResultRow(2, Duration.ofSeconds(3), "battle", 1, 3, 2, 1, 3, true),
//        new ResultRow(2, Duration.ofSeconds(4), "battle", 1, 3, 2, 2, 3, true),
//        new ResultRow(2, Duration.ofSeconds(5), "transaction", 1, 4, 2, 1, 4, true),
//        new ResultRow(2, Duration.ofSeconds(6), "transaction", 1, 4, 2, 2, 4, true)
//    ));
//  }
//
//  @Test
//  void testLastRepeated() throws SQLException, ExecutionException, InterruptedException {
//    DatabaseHelper.createUserActionTable(TableReference.of("login"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(1)),
//        new UserActionRow(2, Duration.ofSeconds(1))
//    ));
//    DatabaseHelper.createUserActionTable(TableReference.of("battle"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(2)),
//        new UserActionRow(2, Duration.ofSeconds(2))
//    ));
//    DatabaseHelper.createUserActionTable(TableReference.of("transaction"), List.of(
//        new UserActionRow(1, Duration.ofSeconds(3)),
//        new UserActionRow(2, Duration.ofSeconds(2)),
//        new UserActionRow(2, Duration.ofSeconds(3)),
//        new UserActionRow(2, Duration.ofSeconds(4))
//    ));
//
//    String sequenceQuery = "login >> battle >> transaction >> transaction";
//    sequenceService.dumpSequenceToTable(new DatetimeInterval(
//            LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
//            LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC"))),
//        sequenceQuery, STEP_REPOSITORY,
//        TableReference.of("sequence_output"));
//
//    assertResult(List.of(
//        new ResultRow(1, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
//        new ResultRow(1, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
//        new ResultRow(1, Duration.ofSeconds(4), "transaction", 1, 3, 2, 2, 3, false),
//        new ResultRow(1, Duration.ofSeconds(5), "battle", 1, 4, 1, 1, 4, false),
//
//        new ResultRow(2, Duration.ofSeconds(1), "login", 1, 1, 1, 1, 1, true),
//        new ResultRow(2, Duration.ofSeconds(2), "battle", 1, 2, 1, 1, 2, true),
//        new ResultRow(2, Duration.ofSeconds(3), "battle", 1, 3, 2, 1, 3, true),
//        new ResultRow(2, Duration.ofSeconds(4), "battle", 1, 3, 2, 2, 3, true),
//        new ResultRow(2, Duration.ofSeconds(5), "transaction", 1, 4, 2, 1, 4, true),
//        new ResultRow(2, Duration.ofSeconds(6), "transaction", 1, 4, 2, 2, 4, true)
//    ));
//  }
}
