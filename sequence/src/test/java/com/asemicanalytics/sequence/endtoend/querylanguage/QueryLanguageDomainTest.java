package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.sequence.SequenceService;
import com.asemicanalytics.sequence.sequence.DomainStep;
import com.asemicanalytics.sequence.sequence.SingleStep;
import com.asemicanalytics.sequence.sequence.StepRepetition;
import com.asemicanalytics.sql.sql.bigquery.BigQueryDialect;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class QueryLanguageDomainTest extends QueryLanguageTestBase {

  @Test
  void testSingleStepInDomain() {
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

  @Test
  void testTwoStepsInDomain() {
    var sequence = SequenceService.parseSequence(
        "domain transaction, login; match battle;", stepLogicalTables);

    assertEquals(List.of(
            new SingleStep("battle", StepRepetition.atLeast(1), 1)),
        sequence.getSteps());

    assertEquals(
        List.of("battle", "login", "transaction"),
        sequence.getDomainActions().stream().map(DomainStep::name).toList());
  }

  @Test
  void testStepFromDomainUsedInMatch() {
    var sequence = SequenceService.parseSequence(
        "domain transaction; match transaction;", stepLogicalTables);

    assertEquals(List.of(
            new SingleStep("transaction", StepRepetition.atLeast(1), 1)),
        sequence.getSteps());

    assertEquals(
        List.of("transaction"),
        sequence.getDomainActions().stream().map(DomainStep::name).toList());
  }

  @Test
  void testDuplicateStepsInDomain() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("domain transaction, transaction; match transaction;",
            stepLogicalTables));
  }

  @Test
  void testDuplicateStepsInDomainDueToAlias() {
    assertThrows(IllegalArgumentException.class, () ->
        SequenceService.parseSequence("domain login, transaction as login; match transaction;",
            stepLogicalTables));
  }

  @Test
  void testAliases() {
    var sequence = SequenceService.parseSequence(
        "domain transaction, transaction as t; match transaction >> t;", stepLogicalTables);

    assertEquals(List.of(
        new SingleStep("transaction", StepRepetition.atLeast(1), 1),
        new SingleStep("t", StepRepetition.atLeast(1), 2)
    ), sequence.getSteps());

    assertEquals(
        List.of("transaction", "t"),
        sequence.getDomainActions().stream().map(DomainStep::name).toList());
  }

  // TODO
  // a = 3 ne radi, al radi 3.0
  // stringovi ne rade, renderuje se ''a'' umesto 'a'
  // vidi jel mozemo nekako da skontamo da je to ime kolone i renderujemo s tabelom
  // ako ucitam kolonu, mozda mozda i neka bolja validacija
  // nema neke vajde o operation precedence, gruni sve binary u isti kos
  @ParameterizedTest
  @CsvSource(value = {
      "a = 3|`transaction`.`a` = 3",
      "a = 3.0|`transaction`.`a` = 3.0",
      "a = 3.0 - 1|`transaction`.`a` = 3.0 - 1",
      "a + b + c|`transaction`.`a` + `transaction`.`b` + `transaction`.`c`",
      "a > 3|`transaction`.`a` > 3",
      "a >= 3|`transaction`.`a` >= 3",
      "a < 3|`transaction`.`a` < 3",
      "a <= 3|`transaction`.`a` <= 3",
      "a != 3|`transaction`.`a` != 3",
      "a = 'a'|`transaction`.`a` = 'a'",
      "a = (1 + 3) AND b = c|`transaction`.`a` = (1 + 3) AND `transaction`.`b` = `transaction`.`c`",
      "a = true and b = false|`transaction`.`a` = true and `transaction`.`b` = false",
      "a or b or c|`transaction`.`a` or `transaction`.`b` or `transaction`.`c`",
      "a is null|`transaction`.`a` IS NULL",
      "a is not null|`transaction`.`a` IS not NULL",
      "a between 2 and (3 + 4)|`transaction`.`a` BETWEEN 2 AND (3 + 4)",
      "duration(a, b) = 5|DURATION(`transaction`.`a`, `transaction`.`b`) = 5",
      "a in (1,2)|`transaction`.`a` IN (1, 2)",
      "a not in (1,2)|`transaction`.`a` not IN (1, 2)",
  }, delimiter = '|')
  void testDomainFilters(String filter, String expected) {
    var sequence = SequenceService.parseSequence(
        "domain transaction where " + filter + "; match transaction;", stepLogicalTables);

    assertEquals(List.of(
            new SingleStep("transaction", StepRepetition.atLeast(1), 1)),
        sequence.getSteps());

    assertEquals(
        List.of("transaction"),
        sequence.getDomainActions().stream().map(DomainStep::name).toList());

    assertEquals(
        expected,
        sequence.getDomainForStep("transaction")
            .filter().get().render(new BigQueryDialect()));
  }


}
