package com.flavormetrics.api.controller;

import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.model.request.LoginRequest;
import com.flavormetrics.api.model.request.RegisterRequest;
import com.flavormetrics.api.model.response.ApiErrorResponse;
import com.flavormetrics.api.model.response.LoginResponse;
import com.flavormetrics.api.model.response.RegisterResponse;
import com.flavormetrics.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "Register a user", description = "Register a user based on request data")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "User account created",
        content = @Content(
          schema = @Schema(implementation = RegisterResponse.class),
          mediaType = "application/json"
        )
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid request data",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class)
        )
      ),
      @ApiResponse(
        responseCode = "409",
        description = "Username is not available",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class)
        )
      ),
    }
  )
  @PostMapping("/signup")
  public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.status(201).body(authService.signup(req));
  }

  @Operation(summary = "Login a user", description = "Login in a user based on request data")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Login success",
        content = @Content(
          schema = @Schema(implementation = LoginResponse.class),
          mediaType = "application/json"
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Invalid request data",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = String.class)
        )
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Bad credentials",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = String.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class)
        )
      ),
    }
  )
  @PostMapping("/login")
  public ResponseEntity<UserDetailsImpl> login(
    @RequestBody LoginRequest req,
    HttpServletResponse res
  ) {
    return ResponseEntity.ok(authService.authenticate(req, res));
  }

  @Operation(summary = "Logout a user", description = "Logout current user")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Logout success",
        content = @Content(
          schema = @Schema(implementation = String.class),
          mediaType = "application/json"
        )
      ),
      @ApiResponse(
        responseCode = "401",
        description = "Authentication is null",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = String.class)
        )
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = ApiErrorResponse.class)
        )
      ),
    }
  )
  @GetMapping("/logout")
  public ResponseEntity<String> logout(HttpServletResponse response) {
    return ResponseEntity.ok(authService.logout(response));
  }
}
