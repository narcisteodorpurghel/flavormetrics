package com.flavormetrics.api.model;

import com.flavormetrics.api.entity.Allergy;
import com.flavormetrics.api.enums.DietaryPreferenceType;
import java.util.List;

public record ProfileFilter(DietaryPreferenceType dietaryPreference, List<Allergy> allergies) {
  public List<String> allergiesToString() {
    return allergies.stream().map(Allergy::getName).toList();
  }
}
