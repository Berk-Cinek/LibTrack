import { Component, inject, signal } from '@angular/core';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';

@Component({
  selector: 'app-loan-id',
  imports: [],
  templateUrl: './loan-id.html'
})
export class LoanId {
  private loanApi = inject(LoanApi);
  loan = signal<Loan | null>(null);
  currentId = signal('');

  loanById(id: string){
    this.loanApi.getLoanById(Number(id)).subscribe(data => this.loan.set(data))
  }
}
