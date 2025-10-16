import { DietaryPreference, Difficulty } from '../constants/recipe.constants';

export type TagDto = {
  id: string;
  name: string;
};

export type IngredientDto = {
  id: string;
  name: string;
  quantity: number;
  unit: UnitType;
};

export type RatingDto = {
  recipeId: string;
  user: string;
  score: number;
};

export type AllergyDto = {
  id: string;
  name: string;
  description: string;
};

export type RecipeDto = {
  id: string;
  name: string;
  instructions: string | null;
  imageUrl: string | null;
  prepTimeMinutes: number;
  cookTimeMinutes: number;
  difficulty: DifficultyType;
  estimatedCalories: number;
  createdAt: Date;
  updatedAt: Date;
  tags: Set<TagDto>;
  ingredients: Set<IngredientDto>;
  ratings: Set<RatingDto>;
  allergies: Set<AllergyDto>;
};

export type DietaryPreferenceType = {
  value: DietaryPreference;
  viewValue: string;
  viewDescription: string;
};

export type DifficultyType = {
  value: Difficulty;
  viewValue: string;
};

export type RecipeFilters = {
  prepTimeMinutes: number;
  cookTimeMinutes: number;
  estimatedCalories: number;
  difficulty: Difficulty;
  dietaryPreference: DietaryPreference;
};
