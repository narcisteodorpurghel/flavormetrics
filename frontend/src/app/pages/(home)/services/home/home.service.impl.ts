import { Injectable } from '@angular/core';
import { HomeService } from './home.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RecipeDto } from '../../../(recipe)/interfaces/recipe.interfaces';

@Injectable()
export class HomeServiceImpl implements HomeService {
  private static readonly API_URL = 'http://localhost:8080/api/v1/recipe/byName';

  constructor(private http: HttpClient) {}

  searchRecipesByName(name: string): Observable<RecipeDto[]> {
    return this.http.get<RecipeDto[]>(`${HomeServiceImpl.API_URL}?name=${name}`, {
      headers: {
        Accept: 'application/json',
      },
    });
  }
}
