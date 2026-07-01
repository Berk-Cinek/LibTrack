import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToPartialLoan } from '../loan-mapper';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';

@Component({
  selector: 'app-loan-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './loan-partial-update.html',
  styleUrl: './loan-partial-update.css',
})
export class LoanPartialUpdate {
  private loanApi = inject(LoanApi);
  private formBuilder = inject(FormBuilder);
  result = signal<Loan | null>(null);
  errorMessage = signal('');
  currentId = signal('');

  loanForm = this.formBuilder.group({
    dueDate: [''],
    status: [''],
  });

  partialUpdateLoan(id: string) {
    const changes = formToPartialLoan(this.loanForm.value);
    this.loanApi.partialUpdate(Number(id), changes).subscribe({
      next: loan => {
        this.errorMessage.set('');
        this.result.set(loan);
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(err.error.message);
      },
    });
  }
}
