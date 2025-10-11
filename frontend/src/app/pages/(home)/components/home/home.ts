import { ChangeDetectionStrategy, Component, effect, Inject, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { HomeService } from '../../services/home/home.service';
import { HOME_SERVICE_TOKEN } from '../../../../di/custom-injections';
import { HomeServiceImpl } from '../../services/home/home.service.impl';
import { RecipeDto } from '../../../(recipe)/interfaces/recipe.interfaces';

@Component({
  selector: 'app-home',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    FormsModule,
    MatIconModule,
    ReactiveFormsModule,
    FormsModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    AsyncPipe,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{ provide: HOME_SERVICE_TOKEN, useClass: HomeServiceImpl }],
})
export class Home {
  readonly cards = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  options: RecipeDto[] = [];
  search: FormControl<string | null> = new FormControl('');
  searchValue = signal(this.search.value);
  filteredStreets: Observable<RecipeDto[]>;

  constructor(@Inject(HOME_SERVICE_TOKEN) private homeService: HomeService) {
    this.filteredStreets = this.search.valueChanges.pipe(
      startWith(''),
      map((value) => this._filter(value || '')),
    );
    effect(() => {
      this.search.valueChanges.pipe(debounceTime(300)).subscribe((res) => {
        this.searchValue.set(res ?? '');
        console.log(res);
        this.homeService.searchRecipesByName(res ?? '').subscribe((res) => {
          this.options = res;
          console.log(res);
        });
      });
    });
  }

  private _filter(value: string): RecipeDto[] {
    const filterValue = this._normalizeValue(value);
    return this.options.filter((option) => this._normalizeValue(option.name).includes(filterValue));
  }

  private _normalizeValue(value: string): string {
    return value.toLowerCase().replace(/\s/g, '');
  }
}
