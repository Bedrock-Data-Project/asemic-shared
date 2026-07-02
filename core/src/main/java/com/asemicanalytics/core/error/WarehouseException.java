package com.asemicanalytics.core.error;

/** A failure talking to the warehouse: connection, auth, timeout, or a rejected query. */
public class WarehouseException extends SystemException {
  public WarehouseException(String message) {
    super(message, null, null, null);
  }

  public WarehouseException(String message, Throwable cause) {
    super(message, cause, null, cause == null ? null : cause.getMessage());
  }

  public WarehouseException(String message, Throwable cause, String detail) {
    super(message, cause, null, detail);
  }

  @Override
  public String kind() {
    return "warehouse";
  }

  @Override
  public boolean retryable() {
    return true;
  }
}
