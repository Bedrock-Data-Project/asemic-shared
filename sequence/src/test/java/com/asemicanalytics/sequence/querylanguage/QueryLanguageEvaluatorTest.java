package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.Sequence;
import java.util.Map;
import org.junit.jupiter.api.Test;

class QueryLanguageEvaluatorTest {

  @Test
  void test() {
    QueryLanguageEvaluator queryLanguageEvaluator = new QueryLanguageEvaluator(Map.of());
    Sequence sequence = queryLanguageEvaluator.parse(null,
        "step1 >> b >> c >> (d, e) >> f");
    System.out.println(sequence);
  }

}
