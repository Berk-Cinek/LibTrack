import { Component, inject, HostListener, ElementRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth-service';
import { LoginWidget } from '../../auth/login-widget/login-widget';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterLink, LoginWidget],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {
  private elementRef = inject(ElementRef);
  authService = inject(AuthService);

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
    if (!clickedInsideNav) {
      // click landed outside the nav entirely — close all dropdowns
      this.closeAll();
      return;
    }

    const allDetails = this.elementRef.nativeElement.querySelectorAll('details');
    allDetails.forEach((details: HTMLDetailsElement) => {
      if (!details.contains(event.target as Node)) {
        details.open = false;
      }
    });
  }

  private closeAll() {
    const allDetails = this.elementRef.nativeElement.querySelectorAll('details');
    allDetails.forEach((d: HTMLDetailsElement) => (d.open = false));
  }
}
