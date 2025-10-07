package com.flavormetrics.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flavormetrics.api.entity.Email;
import com.flavormetrics.api.entity.Rating;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.exception.MaximumNumberOfRatingException;
import com.flavormetrics.api.exception.RecipeNotFoundException;
import com.flavormetrics.api.model.RatingDto;
import com.flavormetrics.api.model.UserDetailsImpl;
import com.flavormetrics.api.repository.RatingRepository;
import com.flavormetrics.api.repository.RecipeRepository;
import com.flavormetrics.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

  private UUID userId;
  private UUID recipeId;
  private User user;
  private Recipe recipe;
  private Rating rating;

  @Mock private UserRepository userRepository;

  @Mock private RecipeRepository recipeRepository;

  @Mock private RatingRepository ratingRepository;

  @InjectMocks private RatingServiceImpl ratingService;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    recipeId = UUID.randomUUID();

    var email = new Email();
    user = new User();
    email.setAddress("test@email.com");
    user.setEmail(email);
    user.setId(userId);
    recipe = new Recipe();
    recipe.setId(recipeId);
    recipe.setRatings(new HashSet<>());

    rating = new Rating();
    rating.setId(UUID.randomUUID());
    rating.setUser(user);
    rating.setRecipe(recipe);

    var principal = new UserDetailsImpl(user);
    SecurityContextHolder.getContext()
        .setAuthentication(new TestingAuthenticationToken(principal, null));
  }

  @Test
  void addRecipeRating_success() {
    when(recipeRepository.getRecipeByIdEager(recipeId)).thenReturn(Optional.of(recipe));
    when(ratingRepository.isRecipeAlreadyRatedByUser(userId, recipeId)).thenReturn(false);
    when(userRepository.getReferenceById(userId)).thenReturn(user);

    Map<String, String> result = ratingService.addRecipeRating(recipeId, 5);

    assertEquals("Recipe has been rated", result.get("message"));
    verify(ratingRepository).save(any(Rating.class));
    verify(recipeRepository).save(recipe);
  }

  @Test
  void addRecipeRating_recipeNotFound_throwsException() {
    when(recipeRepository.getRecipeByIdEager(recipeId)).thenReturn(Optional.empty());

    assertThrows(RecipeNotFoundException.class, () -> ratingService.addRecipeRating(recipeId, 4));
  }

  @Test
  void addRecipeRating_alreadyRated_throwsException() {
    when(recipeRepository.getRecipeByIdEager(recipeId)).thenReturn(Optional.of(recipe));
    when(ratingRepository.isRecipeAlreadyRatedByUser(userId, recipeId)).thenReturn(true);

    assertThrows(
        MaximumNumberOfRatingException.class, () -> ratingService.addRecipeRating(recipeId, 3));
  }

  @Test
  void findAllRatingsByRecipeId_returnsSet() {
    Set<RatingDto> expected = Set.of(new RatingDto(rating));
    when(ratingRepository.findAllByRecipeId(recipeId)).thenReturn(expected);

    Set<RatingDto> result = ratingService.findAllRatingsByRecipeId(recipeId);

    assertEquals(expected, result);
  }

  @Test
  void findAllRatingsByUserId_returnsSet() {
    Set<RatingDto> expected = Set.of(new RatingDto(rating));
    when(ratingRepository.findAllRatingsByUserId(userId)).thenReturn(expected);

    Set<RatingDto> result = ratingService.findAllRatingsByUserId(userId);

    assertEquals(expected, result);
  }
}
