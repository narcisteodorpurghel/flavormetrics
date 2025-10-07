package com.flavormetrics.api.model.request;

import com.flavormetrics.api.enums.DietaryPreferenceType;
import com.flavormetrics.api.model.AllergyDto;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record CreateProfileRequest(
    @NotNull DietaryPreferenceType dietaryPreference, @NotNull Set<AllergyDto> allergies) {}
