package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QueryLanguageTest extends QueryLanguageTestBase {
  @Test
  void testOneValidSequence() {
    assertSteps("match login;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1)
    ));
  }

  @Test
  void testTwoValidSequence() {
    assertSteps("match login >> battle;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2)
    ));
  }

  @Test
  void testThreeValidSequence() {
    assertSteps("match login >> battle >> transaction;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2),
        new SingleStep("transaction", StepRepetition.oneOrMore(), 3)
    ));
  }

  @Test
  void testRepeatingButNotInSequence() {
    assertSteps("match login >> battle >> transaction >> login >> battle;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.oneOrMore(), 2),
        new SingleStep("transaction", StepRepetition.oneOrMore(), 3),
        new SingleStep("login", StepRepetition.oneOrMore(), 4),
        new SingleStep("battle", StepRepetition.oneOrMore(), 5)
    ));
  }

  @Test
  void testGroupSteps() {
    assertSteps("match login >> (battle, transaction);", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new GroupStep(List.of(
            new SingleStep("battle", StepRepetition.oneOrMore(), 2),
            new SingleStep("transaction", StepRepetition.oneOrMore(), 2)
        ))
    ));
  }

  @Test
  void testRepetitions() {
    assertSteps("match login >> battle{1,} >> transaction{2,3} >> battle{1,1};", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.atLeast(1), 2),
        new SingleStep("transaction", StepRepetition.between(2, 3), 3),
        new SingleStep("battle", StepRepetition.exactly(1), 4)
    ));
  }

  @Test
  void testFirstStepShouldNotBeRepeated() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> login;", stepLogicalTables)
    );
  }

  @Test
  void testSecondStepCannotBeRepeatedIfNonLastItemIsRepeatedNonFixed() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> battle >> battle;",
            stepLogicalTables)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> battle{2,3} >> battle;",
            stepLogicalTables)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> battle{2,} >> battle;",
            stepLogicalTables)

    );
  }

  @Test
  void testSecondStepCanBeRepeatedIfNonLastItemIsRepeatedFixed() {
    assertSteps("match login >> battle{1,1} >> battle;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(1), 2),
        new SingleStep("battle", StepRepetition.oneOrMore(), 3)
    ));

    assertSteps("match login >> battle{2,2} >> battle;", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(2), 2),
        new SingleStep("battle", StepRepetition.oneOrMore(), 3)
    ));

    assertSteps("match login >> battle{2,2} >> battle{2,3};", List.of(
        new SingleStep("login", StepRepetition.oneOrMore(), 1),
        new SingleStep("battle", StepRepetition.exactly(2), 2),
        new SingleStep("battle", StepRepetition.between(2, 3), 3)
    ));
  }

  @Test
  void testRepetitionsInStepGroups() {
    assertSteps("match (login{1,}, battle{2,2}, transaction{3,5});", List.of(
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
        SequenceService.parseSequence("match (login, battle, battle{3,3});",
            stepLogicalTables)
    );

    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match (login, battle, battle);",
            stepLogicalTables)
    );
  }

  @Test
  void testDomain() {
    var sequence = SequenceService.parseSequence(
        "domain transaction; match login >> battle;", stepLogicalTables);

    assertEquals(List.of(
            new SingleStep("login", StepRepetition.oneOrMore(), 1),
            new SingleStep("battle", StepRepetition.atLeast(1), 2)),
        sequence.getSteps());

    assertEquals(
        List.of("login", "battle", "transaction"),
        sequence.getDomainActions().stream().map(DomainStep::name).toList());
  }


}
