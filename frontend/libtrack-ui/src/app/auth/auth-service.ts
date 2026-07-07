import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/auth';

  username = signal<string | null>(null);
  role = signal<string | null>(null);
  checked = signal(false);

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
