package com.flavormetrics.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flavormetrics.api.entity.Ingredient;
import com.flavormetrics.api.enums.UnitType;
import java.util.UUID;

public record IngredientDto(@JsonIgnore UUID id, String name, int quantity, UnitType unit) {
  public IngredientDto(Ingredient ingredient) {
    this(ingredient.getId(), ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
  }
}
