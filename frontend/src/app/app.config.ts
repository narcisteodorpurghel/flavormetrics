import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import {
  PreloadAllModules,
  provideRouter,
  withPreloading,
  withViewTransitions,
} from '@angular/router';

import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';
import { provideHomeService } from './pages/(home)/services/home/home.service';
import { provideRecipeService } from './pages/(recipe)/services/recipe.service';
import { provideCloudinaryService } from './shared/services/cloudinary.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideHttpClient(),
    provideHomeService(),
    provideRecipeService(),
    provideCloudinaryService(),
    provideRouter(routes, withPreloading(PreloadAllModules), withViewTransitions()), // pre fetching when the app is in idle mode
  ],
};
