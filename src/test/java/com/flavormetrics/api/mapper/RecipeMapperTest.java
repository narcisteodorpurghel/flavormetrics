package com.flavormetrics.api.mapper;

import static org.assertj.core.api.Assertions.*;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.model.RecipeByOwner;
import com.flavormetrics.api.model.RecipeDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RecipeMapperTest {

  @Test
  void toRecipeByOwner_shouldMapCorrectly() {
    var user = new User();
    var email = new Email();
    email.setAddress("mock-address");
    user.setEmail(email);
    Recipe recipe = new Recipe();
    recipe.setUser(user);
    recipe.setId(UUID.randomUUID());
    recipe.setName("Pizza");
    List<Recipe> recipes = List.of(recipe);
    RecipeByOwner result = RecipeMapper.toRecipeByOwner(recipes, email.getAddress());
    assertThat(result.owner()).isEqualTo(email.getAddress());
    assertThat(result.recipes()).hasSize(1);
    assertThat(result.recipes().getFirst()).isInstanceOf(RecipeDto.class);
  }

  @Test
  void toRecipeByOwner_shouldThrowWhenNull() {
    assertThatThrownBy(() -> RecipeMapper.toRecipeByOwner(null, "owner"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Recipe cannot be null");
  }
}
