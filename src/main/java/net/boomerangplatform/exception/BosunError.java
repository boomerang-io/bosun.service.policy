package net.boomerangplatform.exception;

import java.text.MessageFormat;

public enum BosunError {

  // TODO decide on error codes. Do we have a range for each service and general errors.
  // TODO externalize messages to file so we can implement translation.

  CUSTOM_ERROR(0, "{0}"), POLICY_NOT_FOUND(0, "No Policy found with ID of {0}"), POLICY_DELETED(0,
      "Policy with ID of {0} is marked as deleted.");

  private final int code;
  private final String message;

  BosunError(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage(Object... args) {
    return MessageFormat.format(message, args);
  }

  // TODO decide on message format
  @Override
  public String toString() {
    return "[error] " + code + ": " + message;
  }


}
