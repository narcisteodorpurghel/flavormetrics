package com.flavormetrics.api.mapper;

import com.flavormetrics.api.entity.Profile;
import com.flavormetrics.api.entity.User;
import com.flavormetrics.api.model.AllergyDto;
import com.flavormetrics.api.model.ProfileDto;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProfileMapper {

  private ProfileMapper() {}

  public static ProfileDto toDto(Profile profile) {
    if (profile == null) {
      throw new IllegalArgumentException("Profile cannot be null");
    }

    Set<AllergyDto> allergies = Optional.ofNullable(profile.getAllergies())
      .orElse(Collections.emptySet())
      .stream()
      .map(AllergyDto::new)
      .collect(Collectors.toSet());

    return new ProfileDto(
      profile.getId(),
      profile.getBio(),
      profile.getDietaryPreference(),
      allergies,
      Optional.ofNullable(profile.getUser()).map(User::getId).orElse(null),
      profile.getCreatedAt(),
      profile.getUpdatedAt()
    );
  }
}
