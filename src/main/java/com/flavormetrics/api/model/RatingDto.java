package com.flavormetrics.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flavormetrics.api.entity.Rating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record RatingDto(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID recipeId,
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) String user,
    @Min(0) @Max(5) int score) {
  public RatingDto(Rating rating) {
    this(rating.getId(), rating.getUser().getEmail().getAddress(), rating.getScore());
  }
}
