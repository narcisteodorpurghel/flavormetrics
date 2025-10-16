import { Component, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RecipeDto } from '../../../pages/(recipe)/interfaces/recipe.interfaces';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-recipe-card',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatChipsModule,
  ],
  templateUrl: './recipe-card.html',
  styleUrl: './recipe-card.scss',
})
export class RecipeCard {
  private static readonly DEFAULT_IMAGE_URL =
    'https://ik.imagekit.io/vq8udofpo/flavormetrics/string_ZFXCFzRNR.jpg?updatedAt=1750851551852';

  readonly recipe = input.required<RecipeDto>();

  get defaultImageUrl() {
    return RecipeCard.DEFAULT_IMAGE_URL;
  }
}
