export enum Difficulty {
  easy = 'easy',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
  UNSPECIFIED = 'UNSPECIFIED'
}

export enum DietaryPreference {
  vegetarian = 'vegetarian',
  VEGAN = 'VEGAN',
  FISH_INCLUSIVE = 'FISH_INCLUSIVE',
  KETO = 'KETO',
  PALEO = 'PALEO',
  LOW_CARB = 'LOW_CARB',
  LOW_FAT = 'LOW_FAT',
  HALAL = 'HALAL',
  KOSHER = 'KOSHER',
  DIABETIC_FRIENDLY = 'DIABETIC_FRIENDLY',
  NONE = 'NONE',
  HIGH_PROTEIN = 'HIGH_PROTEIN',
  HIGH_FIBER = 'HIGH_FIBER',
  UNSPECIFIED = 'UNSPECIFIED'
}

export const DietaryPreferenceDescriptions: Record<DietaryPreference, string> = {
  [DietaryPreference.vegetarian]: 'No meat, may include dairy/eggs',
  [DietaryPreference.VEGAN]: 'No animal products at all',
  [DietaryPreference.FISH_INCLUSIVE]: 'Includes fish but no other meat',
  [DietaryPreference.KETO]: 'Low-carb, high-fat diet',
  [DietaryPreference.PALEO]: 'Whole foods, no grains/dairy',
  [DietaryPreference.LOW_CARB]: 'Reduced carb intake',
  [DietaryPreference.LOW_FAT]: 'Reduced fat intake',
  [DietaryPreference.HALAL]: 'Follows Islamic dietary laws',
  [DietaryPreference.KOSHER]: 'Follows Jewish dietary laws',
  [DietaryPreference.DIABETIC_FRIENDLY]: 'Manages blood sugar',
  [DietaryPreference.NONE]: 'No restrictions',
  [DietaryPreference.HIGH_PROTEIN]: 'High protein diet',
  [DietaryPreference.HIGH_FIBER]: 'High fiber intake',
  [DietaryPreference.UNSPECIFIED]: 'Unspecified',
};
