package com.asemicanalytics.core.error;

/** An error that is not the caller's fault: a warehouse failure or an internal bug. */
public abstract class SystemException extends AsemicException {
  protected SystemException(String message, Throwable cause, Position position, String detail) {
    super(message, cause, position, detail);
  }

  @Override
  public Category category() {
    return Category.SYSTEM;
  }
}
