import { Component, inject, input, output, effect } from '@angular/core';
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

  loan = input<Loan | null>(null);
  saved = output<Loan>();

  loanForm = this.formBuilder.group({
    dueDate: [''],
    status: [''],
  });

  constructor() {
    effect(() => {
      const l = this.loan();
      if (l) {
        this.loanForm.patchValue({
          dueDate: l.dueDate,
          status: l.status,
        });
      }
    });
  }

  submit() {
    const l = this.loan();
    if (!l) return;

    const changes = formToPartialLoan(this.loanForm.value);
    this.loanApi.partialUpdate(l.id, changes).subscribe({
      next: updated => this.saved.emit(updated),
      error: (err: HttpErrorResponse) => alert(err.error?.message ?? 'Update failed'),
    });
  }
}
