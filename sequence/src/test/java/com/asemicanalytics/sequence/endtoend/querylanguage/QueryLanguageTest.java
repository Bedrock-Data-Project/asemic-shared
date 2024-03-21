package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.Step;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import com.asemicanalytics.sequence.sequence.StepTable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class QueryLanguageTest {
  private final Map<String, StepTable> stepRepository = Map.of(
      "login", stepTable("login"),
      "battle", stepTable("battle"),
      "transaction", stepTable("transaction")
  );
  private final Duration horizon = Duration.ofDays(1);

  private final DatetimeInterval datetimeInterval = new DatetimeInterval(
      LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
      LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC")));

  private StepTable stepTable(String stepName) {
    return new StepTable(stepName, TableReference.of(stepName), List.of(),
        "user_id", "date_", "ts");
  }

  private void assertSteps(String query, List<Step> expectedSteps) {
    assertEquals(expectedSteps,
        SequenceService.parseSequence(datetimeInterval, query, stepRepository).getSteps());
  }

  @Test
  void testOneValidSequence() {
    assertSteps("login", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1)
    ));
  }

  @Test
  void testTwoValidSequence() {
    assertSteps("login >> battle", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2)
    ));
  }

  @Test
  void testThreeValidSequence() {
    assertSteps("login >> battle >> transaction", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2),
        new SingleStep("transaction", StepRepetition.oneOrMore(), 3)
    ));
  }

  @Test
  void testRepeatingButNotInSequence() {
    assertSteps("login >> battle >> transaction >> login >> battle", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2),
        new SingleStep("transaction", StepRepetition.oneOrMore(), 3),
        new SingleStep("login", StepRepetition.oneOrMore(), 4),
        new SingleStep("battle", StepRepetition.oneOrMore(), 5)
    ));
  }

  @Test
  void testGroupSteps() {
    assertSteps("login >> (battle, transaction)", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new GroupStep(List.of(
            new SingleStep("battle", StepRepetition.oneOrMore(), 2),
            new SingleStep("transaction", StepRepetition.oneOrMore(), 2)
        ))
    ));
  }

  @Test
  void testRepetitions() {
    assertSteps("login >> battle{1,} >> transaction{2,3} >> battle{1,1}", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.atLeast(1), 2),
        new SingleStep("transaction", StepRepetition.between(2, 3), 3),
        new SingleStep("battle", StepRepetition.exactly(1), 4)
    ));
  }

  @Test
  void testFirstStepShouldNotBeRepeated() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "login >> login", stepRepository)
    );
  }

  @Test
  void testSecondStepCannotBeRepeatedIfNonLastItemIsRepeatedNonFixed() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "login >> battle >> battle", stepRepository)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "login >> battle{2,3} >> battle",
            stepRepository)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "login >> battle{2,} >> battle",
            stepRepository)

    );
  }

  @Test
  void testSecondStepCanBeRepeatedIfNonLastItemIsRepeatedFixed() {
    assertSteps("login >> battle{1,1} >> battle", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(1), 2),
        new SingleStep("battle", StepRepetition.oneOrMore(), 3)
    ));

    assertSteps("login >> battle{2,2} >> battle", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(2), 2),
        new SingleStep("battle", StepRepetition.oneOrMore(), 3)
    ));

    assertSteps("login >> battle{2,2} >> battle{2,3}", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(2), 2),
        new SingleStep("battle", StepRepetition.between(2, 3), 3)
    ));
  }

  @Test
  void testRepetitionsInStepGroups() {
    assertSteps("(login{1,}, battle{2,2}, transaction{3,5})", List.of(
        new GroupStep(List.of(
            new SingleStep("login", StepRepetition.atLeast(1), 1),
            new SingleStep("battle", StepRepetition.exactly(2), 1),
            new SingleStep("transaction", StepRepetition.between(3, 5), 1)
        ))
    ));
  }

  @Test
  void testGroupStepsCannotContainDuplicates() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "(login, battle, battle{3,3})", stepRepository)
    );

    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence(datetimeInterval, "(login, battle, battle)", stepRepository)
    );
  }


}
