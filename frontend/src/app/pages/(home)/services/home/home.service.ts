import { Observable, Subject } from 'rxjs';
import { RecipeDto } from '../../../(recipe)/interfaces/recipe.interfaces';
import { DataWithPagination } from '../../../../interfaces/http.interfaces';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Provider } from '@angular/core';

export function provideHomeService(): Provider {
  return {
    provide: HomeService,
    useClass: HomeServiceImpl,
  };
}

@Injectable()
export class HomeServiceImpl implements HomeService {
  private readonly http = inject(HttpClient);

  private static readonly API_URL = 'http://localhost:8080/api/v1/recipe/byName';
  private static readonly page = 0;
  private static readonly size = 10;

  searchRecipesByName$(name: string): Observable<DataWithPagination<RecipeDto[]>> {
    return this.http.get<DataWithPagination<RecipeDto[]>>(
      `${HomeServiceImpl.API_URL}?name=${name}&page=${HomeServiceImpl.page}&size=${HomeServiceImpl.size}`,
      {
        headers: {
          Accept: 'application/json',
        },
      },
    );
  }
}

export abstract class HomeService {
  abstract searchRecipesByName$(name: string): Observable<DataWithPagination<RecipeDto[]>>;
}
