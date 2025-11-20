package com.flavormetrics.api.mapper;

import static org.assertj.core.api.Assertions.*;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.model.RatingDto;
import com.flavormetrics.api.model.RatingWithScore;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingMapperTest {

  private Recipe recipe;
  private User user;
  private Email email;

  @BeforeEach
  void setUp() {
    UUID recipeId = UUID.randomUUID();
    recipe = new Recipe();
    recipe.setId(recipeId);
    user = new User();
    email = new Email();
    email.setAddress("mock-address");
    user.setEmail(email);
  }

  @Test
  void testIf_toRatingDto_mapsCorrectly() {
    Rating rating = new Rating();
    rating.setRecipe(recipe);
    rating.setUser(user);
    rating.setScore(5);
    RatingDto dto = RatingMapper.toRatingDto(rating);
    assertThat(dto.recipeId()).isEqualTo(recipe.getId());
    assertThat(dto.user()).isEqualTo(email.getAddress());
    assertThat(dto.score()).isEqualTo(5);
  }

  @Test
  void testIf_toRatingWithScore_mapsCorrectly() {
    Rating rating = new Rating();
    rating.setRecipe(recipe);
    rating.setScore(3);
    RatingWithScore rws = RatingMapper.toRatingWithScore(rating);
    assertThat(rws.recipeId()).isEqualTo(recipe.getId());
    assertThat(rws.score()).isEqualTo(3);
  }

  @Test
  void testIf_toRatingDto_throwsExceptionWhenNull() {
    assertThatThrownBy(() -> RatingMapper.toRatingDto(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Rating cannot be null");
  }

  @Test
  void testIf_toRatingWithScore_throwsExceptionWhenNull() {
    assertThatThrownBy(() -> RatingMapper.toRatingWithScore(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Rating cannot be null");
  }
}
