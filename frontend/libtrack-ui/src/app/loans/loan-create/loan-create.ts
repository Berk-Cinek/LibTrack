import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToLoan } from '../loan-mapper';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';

@Component({
  selector: 'app-loan-create',
  imports: [ReactiveFormsModule],
  templateUrl: './loan-create.html',
  styleUrl: './loan-create.css',
})
export class LoanCreate {
  private loanApi = inject(LoanApi);
  private formBuilder = inject(FormBuilder);
  result = signal<Loan | null>(null);
  errorMessage = signal('');

  loanForm = this.formBuilder.group({
    memberId: ['', Validators.required],
    bookId: ['', Validators.required],
  });

  createOneLoan() {
    const request = formToLoan(this.loanForm.value);
    this.loanApi.createLoan(request).subscribe({
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
