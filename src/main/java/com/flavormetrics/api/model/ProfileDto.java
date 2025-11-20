package com.flavormetrics.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.NonNull;

public record ProfileDto(
  @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID id,
  String bio,
  DietaryPreferenceType dietaryPreference,
  Set<AllergyDto> allergies,
  @NotNull @JsonProperty(access = JsonProperty.Access.READ_ONLY) UUID userId,
  @JsonProperty(access = JsonProperty.Access.READ_ONLY) LocalDateTime createdAt,
  @JsonProperty(access = JsonProperty.Access.READ_ONLY) LocalDateTime updatedAt
) {
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProfileDto that)) {
      return false;
    }
    return (
      Objects.equals(bio, that.bio) &&
      Objects.equals(userId, that.userId) &&
      dietaryPreference == that.dietaryPreference
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bio, dietaryPreference, userId);
  }

  @Override
  @NonNull
  @SuppressWarnings("StringBufferReplaceableByString")
  public String toString() {
    StringBuilder sb = new StringBuilder("ProfileDto{");
    sb.append("id=").append(id);
    sb.append(", bio='").append(bio).append('\'');
    sb.append(", dietaryPreference=").append(dietaryPreference);
    sb.append(", allergies=").append(allergies == null ? "null" : allergies.size());
    sb.append(", userId=").append(userId);
    sb.append(", createdAt=").append(createdAt);
    sb.append(", updatedAt=").append(updatedAt);
    sb.append('}');
    return sb.toString();
  }
}
