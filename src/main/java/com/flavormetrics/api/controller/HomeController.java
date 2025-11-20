package com.flavormetrics.api.controller;

import com.flavormetrics.api.model.response.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

  @Operation(summary = "Get home page", description = "Get home page data")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Success",
        content = @Content(
          schema = @Schema(implementation = String.class),
          mediaType = "application/json"
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
  @GetMapping
  public ResponseEntity<String> home() {
    return ResponseEntity.ok("FlavorMetricsAPI");
  }
}
