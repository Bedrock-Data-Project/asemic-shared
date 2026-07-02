package com.asemicanalytics.core.error;

/** The caller's request referenced something unknown or was otherwise invalid. */
public class RequestException extends UserException {
  public RequestException(String message) {
    super(message, null, null, null);
  }

  @Override
  public String kind() {
    return "request";
  }
}
