package net.boomerangplatform.exception;

import java.text.MessageFormat;

public enum BosunError {

  CUSTOM_ERROR(0, "{0}"),
	POLICY_NOT_FOUND(0, "No Policy found with ID of {0}"),
	POLICY_DELETED(0, "Policy with ID of {0} is marked as deleted.");

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

  @Override
  public String toString() {
    return "[error] " + code + ": " + message;
  }


}
