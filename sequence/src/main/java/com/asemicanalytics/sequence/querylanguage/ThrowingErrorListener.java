package com.asemicanalytics.sequence.querylanguage;

import com.asemicanalytics.core.error.DslException;
import com.asemicanalytics.core.error.Position;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/** Turns ANTLR lexer/parser syntax errors into a positioned {@link DslException}. */
public class ThrowingErrorListener extends BaseErrorListener {
  public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                          int line, int charPositionInLine, String msg,
                          RecognitionException e) {
    throw new DslException(
        "Invalid sequence query: " + msg, new Position(line, charPositionInLine + 1));
  }
}
