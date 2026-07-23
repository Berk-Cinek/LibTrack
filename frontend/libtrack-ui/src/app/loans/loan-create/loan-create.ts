import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { AuthService } from '../../auth/auth-service'
import { formToLoan } from '../loan-mapper';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';

@Component({
  selector: 'app-loan-create',
  imports: [ReactiveFormsModule],
  templateUrl: './loan-create.html'
})
export class LoanCreate {
  private loanApi = inject(LoanApi);
  private formBuilder = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  authService = inject(AuthService);

  result = signal<Loan | null>(null);
  errorMessage = signal('');

  created = output<Loan>();

  bookId = toSignal(
    this.route.queryParamMap.pipe(map(params => params.get('bookId')))
  );

  loanForm = this.formBuilder.group({
    memberId: ['', Validators.required],
    bookId: [this.bookId() ?? '', Validators.required],
  });

  createOneLoan() {
    const request = formToLoan(this.loanForm.value);
    this.loanApi.createLoan(request).subscribe({
      next: loan => {
        this.errorMessage.set('');
        alert("Created a Loan for user: " + this.loanForm.value);
        this.created.emit(loan);
        this.loanForm.reset();
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage.set(err.error.message);
      },
    });
  }
}
