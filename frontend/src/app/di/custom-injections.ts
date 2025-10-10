import { InjectionToken } from '@angular/core';
import { HomeService } from '../pages/(home)/services/home/home.service';

export const HOME_SERVICE_TOKEN = new InjectionToken<HomeService>('HomeService');
