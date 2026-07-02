package com.asemicanalytics.core.error;

/** The caller's formula/SeQL could not be lexed, parsed, or compiled. */
public class DslException extends UserException {
  public DslException(String message) {
    super(message, null, null, null);
  }

  public DslException(String message, Position position) {
    super(message, null, position, null);
  }

  @Override
  public String kind() {
    return "dsl";
  }
}
