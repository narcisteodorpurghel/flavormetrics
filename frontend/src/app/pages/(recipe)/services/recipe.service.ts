import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Provider } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Pagination } from '../../../interfaces/data.interfaces';
import { DataWithPagination } from '../../../interfaces/http.interfaces';
import { RecipeDto, RecipeFilters } from '../interfaces/recipe.interfaces';

export abstract class RecipeService {
  abstract findAll(filters: RecipeFilters, pagination: Pagination): Observable<RecipeDto[]>;
}

@Injectable()
export class RecipeServiceImpl implements RecipeService {
  private readonly http = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api/v1/recipe';

  findAll(filters: RecipeFilters, pagination: Pagination): Observable<RecipeDto[]> {
    return this.http
      .post<DataWithPagination<RecipeDto[]>>(
        `${this.API_URL}/byFilter?pageNumber=${pagination.page}&pageSize=${pagination.pageSize}`,
        filters,
        {
          headers: {
            Accept: 'application/json',
          },
        },
      )
      .pipe(map((response) => response.data));
  }
}

export function provideRecipeService(): Provider {
  return {
    provide: RecipeService,
    useClass: RecipeServiceImpl,
  };
}
