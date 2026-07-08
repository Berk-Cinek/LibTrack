import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-login-widget',
  imports: [ReactiveFormsModule],
  templateUrl: './login-widget.html',
  styleUrl: './login-widget.css',
})
export class LoginWidget {
  authService = inject(AuthService);       // public — template reads it
  private formBuilder = inject(FormBuilder);

  loginError = signal('');
  registerMode = signal(false);

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  registerForm = this.formBuilder.group({
    memberNo: ['', Validators.required],
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  onLogin() {
    if (this.loginForm.invalid) return;

    this.authService.login({
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!,
    }).subscribe({
      next: () => {
        this.loginError.set('');
        this.loginForm.reset();
      },
      error: (err: HttpErrorResponse) => {
        this.loginError.set(err.error?.message ?? 'Login failed');
      },
    });
  }

  onRegister() {
    if (this.registerForm.invalid) return;
    this.authService.register({
      memberNo: Number(this.registerForm.value.memberNo!),
      username: this.registerForm.value.username!,
      password: this.registerForm.value.password!,
    }).subscribe({
      next: () => {
        alert('Account created, you can log in now.');
        this.registerMode.set(false);
        this.registerForm.reset();
      },
      error: (err: HttpErrorResponse) => {
        alert(err.error?.message ?? 'Registration failed');
      },
    });
  }

  onLogoutClick() {
    this.authService.logout();
  }
}
