import { Injectable } from '@angular/core';
import { HomeService } from './home.service';

@Injectable()
export class HomeServiceImpl implements HomeService {
  searchRecipesByName(name: string): string {
    return name;
  }
}
