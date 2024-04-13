package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.sequence.sequence.Sequence;
import com.asemicanalytics.sql.sql.columnsource.ColumnSource;
import java.time.Duration;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class QueryLanguageEvaluator {
  private final Map<String, ColumnSource> stepColumnSources;

  public QueryLanguageEvaluator(Map<String, ColumnSource> stepColumnSources) {
    this.stepColumnSources = stepColumnSources;
  }

  public Sequence parse(String query) {
    QueryLanguageLexer lexer = new QueryLanguageLexer(CharStreams.fromString(query));
    QueryLanguageParser parser = new QueryLanguageParser(new CommonTokenStream(lexer));
    ParseTree tree = parser.statement();
    VisitorResult result = new SequenceVisitor(stepColumnSources).visit(tree);
    return new Sequence(result.getSteps(), result.getDomain(),
        Duration.ofDays(3), false, stepColumnSources);
  }
}
