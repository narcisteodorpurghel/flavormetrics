package com.flavormetrics.api.mapper;

import static org.assertj.core.api.Assertions.*;

import com.flavormetrics.api.entity.*;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.model.response.RegisterResponse;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserMapperTest {

  @Test
  void testIf_toRegisterResponse_mapsCorrectly() {
    var email = new Email();
    email.setAddress("mock-address");

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(email);
    user.setFirstName("mock-first-name");
    user.setLastName("mock-last-name");

    RegisterResponse response = UserMapper.toRegisterResponse(user);

    assertThat(response.id()).isEqualTo(user.getId());
    assertThat(response.email()).isEqualTo(email.getAddress());
    assertThat(response.firstName()).isEqualTo(user.getFirstName());
    assertThat(response.lastName()).isEqualTo(user.getLastName());
    assertThat(response.role()).isEqualTo(RoleType.ROLE_USER);
  }

  @Test
  void toUserDto_shouldMapCorrectly() {
    UUID recipeId = UUID.randomUUID();

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setPasswordHash("hashed");
    user.setFirstName("mock-first-name");
    user.setLastName("mock-last-name");
    user.setUpdatedAt(LocalDateTime.now());

    Email email = new Email();
    email.setAddress("mock-address");
    user.setEmail(email);

    Profile profile = new Profile();
    profile.setId(UUID.randomUUID());
    user.setProfile(profile);

    Recipe recipe = new Recipe();
    recipe.setId(recipeId);
    user.setRecipes(Set.of(recipe));

    Authority authority = new Authority(RoleType.ROLE_USER);
    user.setAuthorities(Set.of(authority));

    Rating rating = new Rating();
    rating.setRecipe(recipe);
    rating.setScore(4);
    user.setRatings(Set.of(rating));

    UserDto response = UserMapper.toUserDto(user);

    assertThat(response.getId()).isEqualTo(user.getId());
    assertThat(response.getEmail()).isEqualTo(user.getEmail().getAddress());
    assertThat(response.getRecipes()).contains(recipeId);
    assertThat(response.getAuthorities()).contains("ROLE_USER");
    assertThat(response.getRatings()).hasSize(1);
  }

  @Test
  void toRegisterResponse_shouldThrowWhenNull() {
    assertThatThrownBy(() -> UserMapper.toRegisterResponse(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User cannot be null");
  }

  @Test
  void toUserDto_shouldThrowWhenNull() {
    assertThatThrownBy(() -> UserMapper.toUserDto(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User cannot be null");
  }
}
