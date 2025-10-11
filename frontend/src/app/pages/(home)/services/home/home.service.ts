import { Observable } from 'rxjs';
import { RecipeDto } from '../../../(recipe)/interfaces/recipe.interfaces';

export type HomeService = {
  searchRecipesByName(name: string): Observable<RecipeDto[]>;
};
