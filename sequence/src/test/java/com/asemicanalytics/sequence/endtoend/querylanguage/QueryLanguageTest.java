package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.core.DataType;
import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.core.TableReference;
import com.asemicanalytics.core.TimeGrains;
import com.asemicanalytics.core.column.Column;
import com.asemicanalytics.core.datasource.UserActionDatasource;
import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.GroupStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.Step;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import com.asemicanalytics.sql.sql.builder.tablelike.Table;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import com.asemicanalytics.sql.sql.columnsource.TableColumnSource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class QueryLanguageTest {
  private final Map<String, ColumnSource> stepColumnSources = Map.of(
      "login", columnSource("login"),
      "battle", columnSource("battle"),
      "transaction", columnSource("transaction")
  );
  private final Duration horizon = Duration.ofDays(1);

  private final DatetimeInterval datetimeInterval = new DatetimeInterval(
      LocalDate.of(2021, 1, 1).atStartOfDay(ZoneId.of("UTC")),
      LocalDate.of(2021, 1, 3).atStartOfDay(ZoneId.of("UTC")));

  private ColumnSource columnSource(String stepName) {
    return new TableColumnSource(new UserActionDatasource(
        stepName, "", Optional.empty(), TableReference.of(stepName),
        new LinkedHashMap<>(Map.of(
            "date_", Column.ofHidden("date_", DataType.DATE),
            "ts", Column.ofHidden("ts", DataType.DATETIME),
            "user_id", Column.ofHidden("user_id", DataType.STRING)
        )),
        new LinkedHashMap<>(), Map.of(), TimeGrains.min15,
        "date_", "ts", "user_id"),
        new Table(TableReference.of(stepName)));
  }

  private void assertSteps(String query, List<Step> expectedSteps) {
    assertEquals(expectedSteps,
        SequenceService.parseSequence(query, stepColumnSources).getSteps());
  }

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
        SequenceService.parseSequence("match login >> login;", stepColumnSources)
    );
  }

  @Test
  void testSecondStepCannotBeRepeatedIfNonLastItemIsRepeatedNonFixed() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence( "match login >> battle >> battle;",
            stepColumnSources)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> battle{2,3} >> battle;",
            stepColumnSources)
    );
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match login >> battle{2,} >> battle;",
            stepColumnSources)

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
            stepColumnSources)
    );

    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("match (login, battle, battle);",
            stepColumnSources)
    );
  }

  @Test
  void testDomain() {
    var sequence = SequenceService.parseSequence(
        "domain transaction; match login >> battle;", stepColumnSources);

    assertEquals(List.of(
            new SingleStep("login", StepRepetition.oneOrMore(), 1),
            new SingleStep("battle", StepRepetition.atLeast(1), 2)),
        sequence.getSteps());

    assertEquals(List.of("login", "battle", "transaction"), sequence.getDomainActions());
  }


}
