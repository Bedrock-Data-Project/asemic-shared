package com.asemicanalytics.core.error;

/** The caller's semantic-layer config is invalid: bad YAML, unresolved reference, or a cycle. */
public class ConfigException extends UserException {
  public ConfigException(String message) {
    super(message, null, null, null);
  }

  public ConfigException(String message, Throwable cause) {
    super(message, cause, null, null);
  }

  @Override
  public String kind() {
    return "config";
  }
}
