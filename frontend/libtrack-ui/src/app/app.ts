import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('libtrack-ui');

  closeTimers = new Map<HTMLDetailsElement, ReturnType<typeof setTimeout>>();

  scheduleClose(details: HTMLDetailsElement) {
    const timer = setTimeout(() => {
      details.open = false;
    }, 500);
    this.closeTimers.set(details, timer);
  }

  cancelClose(details: HTMLDetailsElement) {
    const timer = this.closeTimers.get(details);
    if (timer) {
      clearTimeout(timer);
      this.closeTimers.delete(details);
    }
  }
}
