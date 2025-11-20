package com.flavormetrics.api.model;

import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RecipeFilter(
  @Min(0) @Max(2000) int prepTimeMinutes,
  @Min(0) @Max(2000) int cookTimeMinutes,
  @Min(0) @Max(2000) int estimatedCalories,
  @NotNull DifficultyType difficulty,
  @NotNull DietaryPreferenceType dietaryPreference
) {}
