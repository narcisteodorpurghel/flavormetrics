package com.flavormetrics.api.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Schema(description = "Register request")
public record RegisterRequest(
    @Schema(description = "Unique identifier", example = "narcis@email.com")
        @Email(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+")
        @NotEmpty
        String email,
    @Schema(description = "User first name", example = "Narcis") @NotEmpty String firstName,
    @Schema(description = "User last name", example = "Purghel") @NotEmpty String lastName,
    @Schema(description = "User credentials", example = "strongPassword")
        @NotEmpty
        @Size(min = 8, max = 1000)
        String password) {}
