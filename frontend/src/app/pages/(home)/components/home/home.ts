import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { debounceTime } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import { Pagination } from '../../../../interfaces/data.interfaces';
import { ReviewCard } from '../../../../shared/components/review-card/review-card';
import { HomeService } from '../../services/home/home.service';

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
    AsyncPipe,
    ReviewCard,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home {
  private readonly homeService = inject(HomeService);

  readonly cards = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  readonly pagination: Pagination = {
    page: 0,
    lastPage: 0,
    pageSize: 10,
  };

  readonly search = new FormControl('');

  cardClicked() {
    console.log('card clicked');
  }

  options$ = this.search.valueChanges.pipe(
    debounceTime(300),
    distinctUntilChanged(),
    map((inputValue) => inputValue?.trim()),
    filter((inputValue) => !!inputValue),
    switchMap((inputValue) => {
      return this.homeService.searchRecipesByName$(inputValue ?? '').pipe(map((res) => res.data));
    }),
    // shareReplay({
    //   refCount: true, // to automatically unsubscribe
    //   bufferSize: 1,
    // }),
  );
}
