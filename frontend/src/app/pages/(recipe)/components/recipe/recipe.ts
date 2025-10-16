import { LiveAnnouncer } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { CloudinaryService } from '../../../../shared/services/cloudinary.service';
import { DietaryPreference, Difficulty } from '../../constants/recipe.constants';
import { UnitType } from '../../enums/home.enums';

@Component({
  selector: 'app-recipe',
  imports: [
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    MatButtonToggleModule,
    MatChipsModule,
    MatProgressBarModule,
    MatCardModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
  ],
  templateUrl: './recipe.html',
  styleUrl: './recipe.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Recipe {
  private formBuilder = inject(FormBuilder);

  readonly dialog = inject(MatDialog);

  openDialog() {
    const dialogRef = this.dialog.open(DialogContentExampleDialog);

    dialogRef.afterClosed().subscribe((result) => {
      console.log(`Dialog result: ${result}`);
    });
  }

  addRecipeForm = this.formBuilder.group({
    name: this.formBuilder.control('', Validators.required),
    instructions: this.formBuilder.control('', Validators.required),
    imageUrl: this.formBuilder.control('', Validators.required),
    prepTimeMinutes: this.formBuilder.control(0, Validators.required),
    cookTimeMinutes: this.formBuilder.control(0, Validators.required),
    estimatedCalories: this.formBuilder.control(0, Validators.required),
    difficulty: this.formBuilder.control(Difficulty.UNSPECIFIED, Validators.required),
    dietaryPreference: this.formBuilder.control(DietaryPreference.UNSPECIFIED, Validators.required),
    tags: this.formBuilder.array([
      this.formBuilder.group({
        name: this.formBuilder.control('', Validators.required),
      }),
    ]),
    ingredients: this.formBuilder.array([
      this.formBuilder.group({
        name: this.formBuilder.control('', Validators.required),
        quantity: this.formBuilder.control(0, Validators.required),
        unit: this.formBuilder.control(UnitType.CLOVES, Validators.required),
      }),
    ]),
  });

  readonly unitValues = Object.values(UnitType);

  readonly reactiveKeywords = signal(['angular', 'how-to', 'tutorial', 'accessibility']);
  readonly formControl = new FormControl(['angular']);

  announcer = inject(LiveAnnouncer);

  removeReactiveKeyword(keyword: string) {
    this.reactiveKeywords.update((keywords) => {
      const index = keywords.indexOf(keyword);
      if (index < 0) {
        return keywords;
      }

      keywords.splice(index, 1);
      this.announcer.announce(`removed ${keyword} from reactive form`);
      return [...keywords];
    });
  }

  addReactiveKeyword(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our keyword
    if (value) {
      this.reactiveKeywords.update((keywords) => [...keywords, value]);
      this.announcer.announce(`added ${value} to reactive form`);
    }

    // Clear the input value
    event.chipInput!.clear();
  }
}

@Component({
  selector: 'app-add-image-dialog',
  templateUrl: 'add-image-dialog.html',
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DialogContentExampleDialog {
  private readonly cloudinaryService = inject(CloudinaryService);

  readonly imagePreview = signal('');

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    console.log(file);

    this.imagePreview.set('asdsad');
    console.log(this.imagePreview());
  }
}
