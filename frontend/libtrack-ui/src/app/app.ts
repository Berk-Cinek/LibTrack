import { Component, signal, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './auth/auth-service';
import { NavBar } from './nav/nav-bar/nav-bar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('libtrack-ui');
  private authService = inject(AuthService);   // private again — template no longer needs it

  ngOnInit() {
    this.authService.checkSession();
  }
}
