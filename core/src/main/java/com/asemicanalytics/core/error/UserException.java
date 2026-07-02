package com.asemicanalytics.core.error;

/** An error caused by the caller's input: bad DSL, request, or config. */
public abstract class UserException extends AsemicException {
  protected UserException(String message, Throwable cause, Position position, String detail) {
    super(message, cause, position, detail);
  }

  @Override
  public Category category() {
    return Category.USER;
  }
}
