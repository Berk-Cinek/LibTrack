import { Component, signal, HostListener, ElementRef, inject } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('libtrack-ui');
  private elementRef = inject(ElementRef);

  closeTimers = new Map<HTMLDetailsElement, ReturnType<typeof setTimeout>>();

  scheduleClose(details: HTMLDetailsElement) {
    const timer = setTimeout(() => { details.open = false; }, 1500);
    this.closeTimers.set(details, timer);
  }

  cancelClose(details: HTMLDetailsElement) {
    const timer = this.closeTimers.get(details);
    if (timer) {
      clearTimeout(timer);
      this.closeTimers.delete(details);
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const clickedInsideNav = this.elementRef.nativeElement.contains(event.target);
    if (!clickedInsideNav) return;

    const allDetails = this.elementRef.nativeElement.querySelectorAll('nav details');
    allDetails.forEach((details: HTMLDetailsElement) => {
      if (!details.contains(event.target as Node)) {
        details.open = false;
      }
    });
  }
}
