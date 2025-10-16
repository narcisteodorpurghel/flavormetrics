import { Routes } from '@angular/router';
import { Home } from './pages/(home)/components/home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'recipes',
    loadComponent: () => import('./pages/(recipe)/components/recipe/recipes'),
  },
  {
    path: 'recipes/:id',
    loadComponent: () =>
      import('./pages/(recipe)/components/recipe/recipe').then((value) => value.Recipe),
  },
];
