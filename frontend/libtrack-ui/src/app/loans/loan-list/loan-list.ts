import { Component, inject, signal, OnInit } from '@angular/core';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';


@Component({
  selector: 'app-loan-list',
  imports: [],
  templateUrl: './loan-list.html',
  styleUrl: './loan-list.css',
})
export class LoanList implements OnInit{
  private loanApi = inject(LoanApi);
  loans = signal< Loan[] >([]);

  ngOnInit() {
    this.loanApi.getLoans().subscribe(data => {
      this.loans.set(data.content);
    })
  }
}
