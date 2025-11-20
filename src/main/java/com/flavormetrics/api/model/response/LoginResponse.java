package com.flavormetrics.api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Login response object example")
public record LoginResponse(
  @Schema(description = "Unique identifier found in database", example = "narcis@email.com")
  String username,
  @Schema(description = "User roles from database", example = "ROLE_USER") List<String> roles,
  @Schema(
    description = "Unique identifier generated based on user details from database. Expiration is set to 24H",
    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo2OTYsInVzZXJuYW1lIjoic29ycmxmeWwiLCJleHAiOjE3NDY2NDMwODR9.FZGNecY9HU4pdHtoUlQYlkFzZtu-qlIAWNWLEwkQanw"
  )
  String token
) {}
