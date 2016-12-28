package com.nitor.plantuml.lambda.exception;

public abstract class StatusCodeException extends RuntimeException {

  public StatusCodeException() {
    super();
  }

  public StatusCodeException(String message) {
    super(message);
  }

  public StatusCodeException(String message, Throwable cause) {
    super(message, cause);
  }

  public abstract String getStatusCode();
}
