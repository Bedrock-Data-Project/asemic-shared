package com.asemicanalytics.core.error;

/** An unexpected internal error (a bug), not caused by the caller. */
public class InternalException extends SystemException {
  public InternalException(String message, Throwable cause) {
    super(message, cause, null, null);
  }

  @Override
  public String kind() {
    return "internal";
  }
}
