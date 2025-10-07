package com.flavormetrics.api.model.response;

import com.flavormetrics.api.model.IngredientDto;
import java.util.List;
import java.util.UUID;

public record AddRecipeResponse(
    UUID id, String name, String nutritionist, List<IngredientDto> ingredients) {}
