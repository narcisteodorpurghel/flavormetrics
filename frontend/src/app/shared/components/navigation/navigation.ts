import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { Logo } from '../logo/logo';
import { RouterLink, RouterOutlet } from '@angular/router';

type NavLink = {
  label: string;
  link: string;
};

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
    RouterLink,
  ],
  templateUrl: './navigation.html',
  styleUrl: './navigation.scss',
})
export class Navigation {
  links: NavLink[] = [
    {
      link: 'recipes',
      label: 'Recipes',
    },
    {
      link: 'share',
      label: 'Share',
    },
    {
      link: 'community',
      label: 'Community',
    },
  ];
  activeLink = this.links[0].link;
}
