package com.asemicanalytics.core.error;

/**
 * Base type for engine errors that carry a user-vs-system classification, so a caller can be told
 * whether they need to fix their own input or whether it is a system/warehouse problem.
 */
public abstract class AsemicException extends RuntimeException {

  /** Whose problem the error is: the caller's input, or the system/warehouse. */
  public enum Category {
    USER,
    SYSTEM
  }

  private final Position position;
  private final String detail;

  protected AsemicException(String message, Throwable cause, Position position, String detail) {
    super(message, cause);
    this.position = position;
    this.detail = detail;
  }

  /** USER = the caller did something wrong; SYSTEM = not the caller's fault. */
  public abstract Category category();

  /** Fine-grained kind, e.g. {@code dsl}, {@code request}, {@code config}, {@code warehouse}. */
  public abstract String kind();

  /** Whether retrying the same request might succeed (transient system problems). */
  public boolean retryable() {
    return false;
  }

  /** Source position of the error (DSL line/column), or {@code null} if not applicable. */
  public Position position() {
    return position;
  }

  /** Extra context not meant as the primary message (e.g. raw driver text); may be {@code null}. */
  public String detail() {
    return detail;
  }
}
