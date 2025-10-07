package com.flavormetrics.api.model.response;

public record ApiErrorResponse(int code, String message, String details, String path) {
  public static ApiErrorResponse from(int code, String msg, String details, String path) {
    return new ApiErrorResponse(code, msg, details, path);
  }
}
