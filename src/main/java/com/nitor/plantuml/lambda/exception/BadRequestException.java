package com.nitor.plantuml.lambda.exception;

public class BadRequestException extends StatusCodeException {

  private final static String STATUS_CODE = "400";

  public BadRequestException() {
    super();
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getStatusCode() {
    return STATUS_CODE;
  }
}
