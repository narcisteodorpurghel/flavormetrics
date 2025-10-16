import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { BehaviorSubject, switchMap } from 'rxjs';
import { RecipeCard } from '../../../../shared/components/recipe-card/recipe-card';
import {
  DietaryPreference,
  DietaryPreferenceDescriptions,
  Difficulty,
} from '../../constants/recipe.constants';
import { DietaryPreferenceType, DifficultyType } from '../../interfaces/recipe.interfaces';
import { RecipeService } from '../../services/recipe.service';

@Component({
  selector: 'app-recipes',
  imports: [
    RecipeCard,
    AsyncPipe,
    MatButtonModule,
    MatSidenavModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    ReactiveFormsModule,
    MatPaginatorModule,
    MatCheckboxModule,
  ],
  templateUrl: './recipes.html',
  styleUrl: './recipes.scss',
})
export default class Recipes {
  private static readonly PAGE_SIZE_OPTIONS = [5, 10, 25, 100];
  private static readonly SORT_OPTIONS = ['ASCENDENT', 'DESCENDENT'];

  private readonly recipeService = inject(RecipeService);
  private readonly filters$ = new BehaviorSubject<void>(undefined);

  readonly prepTimeMinutes = new FormControl(0);
  readonly cookTimeMinutes = new FormControl(0);
  readonly estimatedCalories = new FormControl(0);

  readonly dietaryPreferencesOptions: DietaryPreferenceType[] = [
    {
      value: DietaryPreference.vegetarian,
      viewValue: DietaryPreference.vegetarian,
      viewDescription: DietaryPreferenceDescriptions.vegetarian,
    },
    {
      value: DietaryPreference.VEGAN,
      viewValue: DietaryPreference.VEGAN,
      viewDescription: DietaryPreferenceDescriptions.VEGAN,
    },
    {
      value: DietaryPreference.FISH_INCLUSIVE,
      viewValue: DietaryPreference.FISH_INCLUSIVE,
      viewDescription: DietaryPreferenceDescriptions.FISH_INCLUSIVE,
    },
    {
      value: DietaryPreference.KETO,
      viewValue: DietaryPreference.KETO,
      viewDescription: DietaryPreferenceDescriptions.KETO,
    },
    {
      value: DietaryPreference.PALEO,
      viewValue: DietaryPreference.PALEO,
      viewDescription: DietaryPreferenceDescriptions.PALEO,
    },
    {
      value: DietaryPreference.LOW_CARB,
      viewValue: DietaryPreference.LOW_CARB,
      viewDescription: DietaryPreferenceDescriptions.LOW_CARB,
    },
    {
      value: DietaryPreference.LOW_FAT,
      viewValue: DietaryPreference.LOW_FAT,
      viewDescription: DietaryPreferenceDescriptions.LOW_FAT,
    },
  ];

  readonly difficultyOptions: DifficultyType[] = [
    {
      value: Difficulty.easy,
      viewValue: Difficulty.easy,
    },
    {
      value: Difficulty.MEDIUM,
      viewValue: Difficulty.MEDIUM,
    },
    {
      value: Difficulty.HARD,
      viewValue: Difficulty.HARD,
    },
    {
      value: Difficulty.UNSPECIFIED,
      viewValue: Difficulty.UNSPECIFIED,
    },
  ];

  isSideBarActive = false;
  showFiller = false;

  readonly pageSize = signal(10);
  readonly currentPage = signal(0);
  readonly findRecipesBtnVisible = signal(true);

  readonly recipes = this.filters$.pipe(
    switchMap(() =>
      this.recipeService.findAll(
        {
          cookTimeMinutes: this.cookTimeMinutes.value ?? 10000,
          estimatedCalories: this.estimatedCalories.value ?? 10000,
          prepTimeMinutes: this.prepTimeMinutes.value ?? 10000,
          difficulty: Difficulty.easy,
          dietaryPreference: DietaryPreference.vegetarian,
        },
        {
          page: this.currentPage(),
          pageSize: this.pageSize(),
          lastPage: 0,
        },
      ),
    ),
  );

  get pageSizeOptions() {
    return Recipes.PAGE_SIZE_OPTIONS;
  }

  get sortOptions() {
    return Recipes.SORT_OPTIONS;
  }

  toggleSideNav() {
    this.isSideBarActive = !this.isSideBarActive;
  }

  toggleFindRecipesBtn() {
    this.findRecipesBtnVisible.set(!this.findRecipesBtnVisible());
  }

  onPageChange(event: PageEvent) {
    this.pageSize.set(event.pageSize);
    this.currentPage.set(event.pageIndex);
    console.log(this.currentPage());
    this.applyFilters();
  }

  applyFilters() {
    console.log(this.prepTimeMinutes.value);
    console.log(this.cookTimeMinutes.value);
    console.log(this.estimatedCalories.value);
    this.filters$.next();
  }
}
