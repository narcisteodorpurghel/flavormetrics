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
  instructions: string;
  imageUrl: string;
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
