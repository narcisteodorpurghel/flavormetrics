package com.flavormetrics.api.controller;

import com.flavormetrics.api.model.ProfileDto;
import com.flavormetrics.api.model.projection.ProfileProjection;
import com.flavormetrics.api.model.request.CreateProfileRequest;
import com.flavormetrics.api.model.response.ApiErrorResponse;
import com.flavormetrics.api.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

  private final ProfileService profileService;

  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @Operation(
      summary = "Get user profile",
      description = "Get associated user profile from authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = ProfileProjection.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "204",
            description = "Operation success but user doesn't have an associated profile yet",
            content =
                @Content(
                    schema = @Schema(implementation = ObjectUtils.Null.class),
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
  public ResponseEntity<ProfileDto> getProfileById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(profileService.findById(id));
  }

  @Operation(
      summary = "Add user profile",
      description = "Associate a profile to current user from authentication")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation success",
            content =
                @Content(
                    schema = @Schema(implementation = ProfileProjection.class),
                    mediaType = "application/json")),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class))),
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
  @PostMapping
  public ResponseEntity<UUID> create(@RequestBody @Valid CreateProfileRequest req) {
    return ResponseEntity.ok(profileService.create(req));
  }

  @PutMapping("/update")
  public ResponseEntity<ProfileDto> update(@RequestBody @Valid CreateProfileRequest req) {
    return ResponseEntity.ok(profileService.updateById(req));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete() {
    profileService.remove();
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
