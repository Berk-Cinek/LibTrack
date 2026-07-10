import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { environment } from '../../environments/environment';

interface LoginResponse {
  username: string;
  role: string;
}

interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/auth`;

  username = signal<string | null>(null);
  role = signal<string | null>(null);
  checked = signal(false);
  authNotice = signal('');

  constructor() {
    let skipNext = false;

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      if (skipNext) {
        this.authNotice.set('');
        skipNext = false;
      }
      if (this.authNotice()) {
        skipNext = true;
      }
    });
  }

  isLoggedIn = () => this.username() !== null;
  isAdmin = () => this.role() === 'ADMIN';

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, request, { withCredentials: true }).pipe(
      tap(response => {
        this.username.set(response.username);
        this.role.set(response.role);
      })
    );
  }

  logout() {
    this.http.post(`${this.baseUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.username.set(null);
        this.role.set(null);
      },
      error: (err) => {
        console.log('logout FAILED:', err);
      },
    });
  }

  register(request: { memberNo: number; username: string; password: string }): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/register`, request);
  }

  promoteToAdmin(memberId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/members/${memberId}/promote`, {});
  }

  checkSession() {
    this.http.get<LoginResponse>(`${this.baseUrl}/me`, { withCredentials: true }).subscribe({
      next: response => {
        this.username.set(response.username);
        this.role.set(response.role);
        this.checked.set(true);
      },
      error: () => {
        this.username.set(null);
        this.role.set(null);
        this.checked.set(true);
      },
    });
  }
}
