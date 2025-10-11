package com.flavormetrics.api.exception;

import com.flavormetrics.api.model.response.ApiErrorResponse;
import com.flavormetrics.api.model.response.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(TypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(
      TypeMismatchException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            401, HttpStatus.BAD_REQUEST.name(), e.getMessage(), request.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            401, HttpStatus.BAD_REQUEST.name(), e.getMessage(), request.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(
      value = {
        EmailInUseException.class,
        ProfileExistsException.class,
        MaximumNumberOfRatingException.class,
      })
  public ResponseEntity<ApiErrorResponse> handleConflictExceptions(
      RuntimeException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            409, HttpStatus.CONFLICT.name(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(response.code()).body(response);
  }

  @ExceptionHandler(value = {RecipeNotFoundException.class, ProfileNotFoundException.class})
  public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(
      RuntimeException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            409, HttpStatus.NOT_FOUND.name(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(response.code()).body(response);
  }

  @ExceptionHandler(value = {UnAuthorizedException.class})
  public ResponseEntity<ApiErrorResponse> handleAuthExceptions(
      UnAuthorizedException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            401, HttpStatus.UNAUTHORIZED.name(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(response.code()).body(response);
  }

  @ExceptionHandler(value = InvalidImageException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidFileException(
      InvalidImageException e, HttpServletRequest request) {
    var response =
        new ApiErrorResponse(
            400, HttpStatus.BAD_REQUEST.name(), e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(response.code()).body(response);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    Map<String, String> details = new HashMap<>();
    for (FieldError error : e.getFieldErrors()) {
      details.put(error.getField(), error.getDefaultMessage());
    }
    var response =
        new ValidationError(400, HttpStatus.BAD_REQUEST.name(), details, request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(value = HandlerMethodValidationException.class)
  public ResponseEntity<ValidationError> handleHandlerMethodValidationException(
      HandlerMethodValidationException e, HttpServletRequest request) {
    TreeMap<String, String> details = new TreeMap<>();
    for (ParameterValidationResult error : e.getParameterValidationResults()) {
      if (error.getArgument() != null) {
        details.put(
            error.getMethodParameter().getParameterName(),
            error.getResolvableErrors().getFirst().getDefaultMessage());
      } else {
        details.put(error.getMethodParameter().getParameterName(), "null");
      }
    }
    var response =
        new ValidationError(400, HttpStatus.BAD_REQUEST.name(), details, request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
