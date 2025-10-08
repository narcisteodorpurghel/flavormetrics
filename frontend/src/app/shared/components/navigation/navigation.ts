import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { Logo } from '../logo/logo';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-navigation',
  imports: [
    RouterOutlet,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    Logo,
    MatTabsModule,
    RouterOutlet,
    MatCardModule,
  ],
  templateUrl: './navigation.html',
  styleUrl: './navigation.scss',
})
export class Navigation {
  links = ['Recipes', 'Share', 'Community'];
  cards = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  activeLink = this.links[0];
}
