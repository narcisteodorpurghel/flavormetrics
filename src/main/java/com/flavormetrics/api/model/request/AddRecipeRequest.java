package com.flavormetrics.api.model.request;

import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
import com.flavormetrics.api.model.AllergyDto;
import com.flavormetrics.api.model.IngredientDto;
import com.flavormetrics.api.model.TagDto;
import jakarta.validation.constraints.*;
import java.util.Set;

public record AddRecipeRequest(
  @NotBlank String name,
  @NotEmpty Set<IngredientDto> ingredients,
  String imageUrl,
  @NotBlank @Size(min = 15, max = 2000) String instructions,
  @Min(0) @Max(2000) int prepTimeMinutes,
  @Min(0) @Max(2000) int cookTimeMinutes,
  @NotNull DifficultyType difficulty,
  @Min(0) @Max(2000) int estimatedCalories,
  @NotNull Set<TagDto> tags,
  @NotNull Set<AllergyDto> allergies,
  @NotNull DietaryPreferenceType dietaryPreference
) {}
