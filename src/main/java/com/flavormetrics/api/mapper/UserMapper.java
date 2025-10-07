package com.flavormetrics.api.mapper;

import com.flavormetrics.api.entity.Authority;
import com.flavormetrics.api.entity.Recipe;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.enums.RoleType;
import com.flavormetrics.api.model.RatingWithScore;
import com.flavormetrics.api.model.UserDto;
import com.flavormetrics.api.model.response.RegisterResponse;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UserMapper {

  private UserMapper() {}

  public static RegisterResponse toRegisterResponse(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    return RegisterResponse.builder()
        .id(user.getId())
        .email(user.getEmail() == null ? null : user.getEmail().getAddress())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(RoleType.ROLE_USER)
        .id(user.getId())
        .build();
  }

  public static UserDto toUserDto(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    Set<RatingWithScore> ratings =
        Optional.ofNullable(user.getRatings()).orElse(Collections.emptySet()).stream()
            .map(RatingMapper::toRatingWithScore)
            .collect(Collectors.toSet());
    Set<UUID> recipes =
        Optional.ofNullable(user.getRecipes()).orElse(Collections.emptySet()).stream()
            .map(Recipe::getId)
            .collect(Collectors.toSet());
    Set<String> authorities =
        Optional.ofNullable(user.getAuthorities()).orElse(Collections.emptySet()).stream()
            .map(Authority::getAuthority)
            .collect(Collectors.toSet());
    return new UserDto(
        user.getId(),
        user.getPasswordHash(),
        user.getFirstName(),
        user.getLastName(),
        user.isAccountNonExpired(),
        user.isAccountNonLocked(),
        user.isCredentialsNonExpired(),
        user.isEnabled(),
        user.getUpdatedAt(),
        user.getCreatedAt(),
        user.getEmail() != null ? user.getEmail().getAddress() : null,
        user.getProfile() != null ? user.getProfile().getId() : null,
        recipes,
        authorities,
        ratings);
  }
}
