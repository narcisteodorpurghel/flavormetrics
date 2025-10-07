package com.flavormetrics.api.controller;

import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.model.response.ApiErrorResponse;
import com.flavormetrics.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class AdminController {

  private final UserService userService;

  public AdminController(UserService userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "Get all registered users from database",
      description = "Requires to be authenticated as admin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = List.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/all")
  public ResponseEntity<Set<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.findAllUsers());
  }

  @Operation(
      summary = "Get a user from database",
      description = "Requires to be authenticated as admin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(userService.findUserById(id));
  }

  @Operation(summary = "Lock a user", description = "Requires to be authenticated as admin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = UserDetailsImpl.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PatchMapping("/lock/{id}")
  public ResponseEntity<UserDetailsImpl> lockUserById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(userService.lockUserById(id));
  }

  @Operation(summary = "Unlock a user", description = "Requires to be authenticated as admin")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = UserDetailsImpl.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @PatchMapping("/unlock/{id}")
  public ResponseEntity<UserDetailsImpl> unlockUserById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(userService.unlockUserById(id));
  }

  @Operation(summary = "Delete a user", description = "Requires to be authenticated as admin")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Operation success"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthenticated",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "403",
            description = "Unauthorized",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
      })
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<UserDto> deleteUserById(@PathVariable("id") UUID id) {
    userService.deleteUserById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
