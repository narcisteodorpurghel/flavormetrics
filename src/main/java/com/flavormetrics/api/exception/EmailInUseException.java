package com.flavormetrics.api.exception;

public class EmailInUseException extends RuntimeException {
  public EmailInUseException(String email) {
    super("Email %s is not available".formatted(email));
  }
}
