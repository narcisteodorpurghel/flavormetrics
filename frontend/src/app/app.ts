import { Component, signal } from '@angular/core';
import { Navigation } from './shared/components/navigation/navigation';

@Component({
  selector: 'app-root',
  imports: [Navigation],
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('frontend');
}
