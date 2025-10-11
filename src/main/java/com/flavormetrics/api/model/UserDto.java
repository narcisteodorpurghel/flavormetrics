package com.flavormetrics.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.*;

public class UserDto {
  private final UUID id;
  @JsonIgnore private final String passwordHash;
  private final String firstName;
  private final String lastName;
  private final boolean isAccountNonExpired;
  private final boolean isAccountNonLocked;
  private final boolean isCredentialsNonExpired;
  private final boolean isEnabled;
  private final LocalDateTime updatedAt;
  private final LocalDateTime createdAt;
  private final String email;
  private final UUID profileId;
  private final Set<UUID> recipes;
  private final Set<String> authorities;
  private final Set<RatingWithScore> ratings;

  public UserDto(
      UUID id,
      String passwordHash,
      String firstName,
      String lastName,
      boolean isAccountNonExpired,
      boolean isAccountNonLocked,
      boolean isCredentialsNonExpired,
      boolean isEnabled,
      LocalDateTime updatedAt,
      LocalDateTime createdAt,
      String email,
      UUID profileId,
      Set<UUID> recipes,
      Set<String> authorities,
      Set<RatingWithScore> ratings) {
    this.id = id;
    this.passwordHash = passwordHash;
    this.firstName = firstName;
    this.lastName = lastName;
    this.isAccountNonExpired = isAccountNonExpired;
    this.isAccountNonLocked = isAccountNonLocked;
    this.isCredentialsNonExpired = isCredentialsNonExpired;
    this.isEnabled = isEnabled;
    this.updatedAt = updatedAt;
    this.createdAt = createdAt;
    this.email = email;
    this.profileId = profileId;
    this.recipes = recipes;
    this.authorities = authorities;
    this.ratings = ratings;
  }

  public UUID getId() {
    return id;
  }

  @JsonIgnore
  public String getPasswordHash() {
    return passwordHash;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public boolean isAccountNonExpired() {
    return isAccountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return isAccountNonLocked;
  }

  public boolean isCredentialsNonExpired() {
    return isCredentialsNonExpired;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getEmail() {
    return email;
  }

  public UUID getProfileId() {
    return profileId;
  }

  public Set<String> getAuthorities() {
    return Optional.ofNullable(authorities).map(Set::copyOf).orElse(Collections.emptySet());
  }

  public Set<RatingWithScore> getRatings() {
    return Optional.ofNullable(ratings).map(Set::copyOf).orElse(Collections.emptySet());
  }

  public Set<UUID> getRecipes() {
    return Optional.ofNullable(recipes).map(Set::copyOf).orElse(Collections.emptySet());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserDto userDto)) {
      return false;
    }
    return (isAccountNonExpired == userDto.isAccountNonExpired
        && isAccountNonLocked == userDto.isAccountNonLocked
        && isCredentialsNonExpired == userDto.isCredentialsNonExpired
        && isEnabled == userDto.isEnabled
        && Objects.equals(id, userDto.id)
        && Objects.equals(firstName, userDto.firstName)
        && Objects.equals(lastName, userDto.lastName)
        && Objects.equals(updatedAt, userDto.updatedAt)
        && Objects.equals(createdAt, userDto.createdAt)
        && Objects.equals(email, userDto.email));
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        firstName,
        lastName,
        isAccountNonExpired,
        isAccountNonLocked,
        isCredentialsNonExpired,
        isEnabled,
        updatedAt,
        createdAt,
        email);
  }

  @Override
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("UserDto{");
    sb.append("id=").append(id);
    sb.append(", firstName='").append(firstName).append('\'');
    sb.append(", lastName='").append(lastName).append('\'');
    sb.append(", isAccountNonExpired=").append(isAccountNonExpired);
    sb.append(", isAccountNonLocked=").append(isAccountNonLocked);
    sb.append(", isCredentialsNonExpired=").append(isCredentialsNonExpired);
    sb.append(", isEnabled=").append(isEnabled);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", email='").append(email).append('\'');
    sb.append(", profileId=").append(profileId);
    sb.append(", authorities=")
        .append(Optional.ofNullable(authorities).orElse(Collections.emptySet()).size());
    sb.append(", ratings=")
        .append(Optional.ofNullable(ratings).orElse(Collections.emptySet()).size());
    sb.append(", recipes=")
        .append(Optional.ofNullable(recipes).orElse(Collections.emptySet()).size());
    sb.append('}');
    return sb.toString();
  }
}
