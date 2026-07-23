import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { LoanApi } from '../loan-api';

@Component({
  selector: 'app-loan-delete',
  imports: [],
  templateUrl: './loan-delete.html'
})
export class LoanDelete {
  private loanApi = inject(LoanApi);
  message = signal('');
  currentId = signal('');

  deleteLoan(id: string) {
    this.loanApi.deleteLoan(Number(id)).subscribe({
      next: (response: HttpResponse<void>) => {
        this.message.set('Loan deleted successfully');
      },
      error: (err: HttpErrorResponse) => {
        this.message.set(err.error.message);
      },
    });
  }
}
