package com.flavormetrics.api.model;

import com.flavormetrics.api.enums.DifficultyType;

public record RecipeDefaultFilter(
    int prepTimeMinutes, int cookTimeMinutes, int estimatedCalories, DifficultyType difficulty) {}
