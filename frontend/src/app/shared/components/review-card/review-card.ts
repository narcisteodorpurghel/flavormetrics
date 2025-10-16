import { Component, output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-review-card',
  imports: [MatCardModule],
  templateUrl: './review-card.html',
  host: {
    '(click)': 'cardClicked.emit()',
  },
  styleUrl: './review-card.scss',
})
export class ReviewCard {
  cardClicked = output<void>();
}
