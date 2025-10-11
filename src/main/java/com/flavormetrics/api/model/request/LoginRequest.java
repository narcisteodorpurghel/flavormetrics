package com.flavormetrics.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request object example")
public record LoginRequest(
    @Schema(description = "Unique identifier used in registration", example = "narcispurghel@example.com")
        String email,
    @Schema(description = "User credentials used in registration", example = "strongPassword")
        String password) {}
