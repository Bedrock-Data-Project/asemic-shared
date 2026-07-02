package com.asemicanalytics.sequence.endtoend.querylanguage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.asemicanalytics.core.error.DslException;
import com.asemicanalytics.sequence.SequenceService;
import org.junit.jupiter.api.Test;

public class SequenceSyntaxErrorTest extends QueryLanguageTestBase {

  @Test
  void malformedQueryThrowsDslExceptionWithPosition() {
    DslException e = assertThrows(DslException.class,
        () -> SequenceService.parseSequence("match >> battle;", stepLogicalTables));
    assertNotNull(e.position());
    assertEquals(1, e.position().line());
  }

  @Test
  void missingSeparatorThrowsDslException() {
    assertThrows(DslException.class,
        () -> SequenceService.parseSequence("match login", stepLogicalTables));
  }
}
