package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.DatetimeInterval;
import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sequence.sequence.StepTable;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class QueryLanguageEvaluator {
  private final Map<String, StepTable> stepRepository;

  public QueryLanguageEvaluator(Map<String, StepTable> stepRepository) {
    this.stepRepository = stepRepository;
  }

  public Sequence parse(DatetimeInterval datetimeInterval, String query) {
    QueryLanguageLexer lexer = new QueryLanguageLexer(CharStreams.fromString(query));
    QueryLanguageParser parser = new QueryLanguageParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.sequence();
    VisitorResult result = new SequenceVisitor().visit(tree);
    return new Sequence(result.getSteps(), Set.of(), datetimeInterval, null,
        false, stepRepository);
  }
}
