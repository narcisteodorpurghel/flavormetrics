package com.flavormetrics.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.enums.DifficultyType;
import com.flavormetrics.api.model.RatingDto;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RatingRepositoryTest {

  private static final String EMAIL_ADDRESS = "mock-address@mock.com";

  @Autowired
  private RatingRepository ratingRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RecipeRepository recipeRepository;

  private UUID userId;
  private UUID recipeId;

  @BeforeEach
  void setUp() {
    Email email = new Email();
    email.setAddress(EMAIL_ADDRESS);

    User user = new User();
    user.setEmail(email);
    user.setFirstName("mock-first-name");
    user.setLastName("mock-last-name");
    user.setPasswordHash("mock-password");
    user = userRepository.save(user);
    userId = user.getId();

    Recipe recipe = new Recipe();
    recipe.setName("mock-recipe-name");
    recipe.setUser(user);
    recipe.setDietaryPreferences(DietaryPreferenceType.diabetic_friendly);
    recipe.setInstructions("mock-instruction mock-instruction mock-instruction");
    recipe.setDifficulty(DifficultyType.easy);
    recipe = recipeRepository.save(recipe);
    recipeId = recipe.getId();
  }

  @Test
  void test_findAllRatingsByUserId_ReturnsEmpty() {
    Set<RatingDto> result = ratingRepository.findAllRatingsByUserId(userId);
    assertTrue(result.isEmpty());
  }

  @Test
  void restIf_findAllByRecipeId_ReturnsEmpty() {
    Set<RatingDto> result = ratingRepository.findAllByRecipeId(UUID.randomUUID());
    assertTrue(result.isEmpty());
  }

  @Test
  void test_findAllRatingsByUserId_ReturnsNotEmpty() {
    Rating rating = new Rating();
    rating.setUser(userRepository.findById(userId).orElseThrow());
    rating.setRecipe(recipeRepository.findById(recipeId).orElseThrow());
    rating.setScore(5);
    ratingRepository.save(rating);
    Set<RatingDto> result = ratingRepository.findAllRatingsByUserId(userId);
    assertFalse(result.isEmpty());
    RatingDto dto = result.iterator().next();
    assertEquals(recipeId, dto.recipeId());
    assertEquals(EMAIL_ADDRESS, dto.user());
    assertEquals(5, dto.score());
  }

  @Test
  void testIf_isRecipeAlreadyRatedByUser_ReturnsFalse() {
    boolean result = ratingRepository.isRecipeAlreadyRatedByUser(UUID.randomUUID(), recipeId);
    assertFalse(result);
  }

  @Test
  void testIf_isRecipeAlreadyRatedByUser_ReturnsTrue() {
    Rating rating = new Rating();
    rating.setUser(userRepository.findById(userId).orElseThrow());
    rating.setRecipe(recipeRepository.findById(recipeId).orElseThrow());
    rating.setScore(4);
    ratingRepository.save(rating);
    boolean result = ratingRepository.isRecipeAlreadyRatedByUser(userId, recipeId);
    assertTrue(result);
  }
}
