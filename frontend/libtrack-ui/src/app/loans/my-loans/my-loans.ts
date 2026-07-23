import { Component, inject, signal, effect } from '@angular/core';
import { LoanApi } from '../loan-api';
import { AuthService } from '../../auth/auth-service';
import { Loan } from '../loan';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-my-loans',
  imports: [NgClass],
  templateUrl: './my-loans.html'
})
export class MyLoans {
  private loanApi = inject(LoanApi);
  authService = inject(AuthService);
  loans = signal<Loan[]>([]);

  constructor() {
    effect(() => {
      if (this.authService.isLoggedIn()) {
        this.loanApi.getMyLoans().subscribe({
          next: data => this.loans.set(data.content),
          error: () => this.loans.set([]),
        });
      } else {
        this.loans.set([]);
      }
    });
  }
}
