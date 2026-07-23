import { Component, inject, signal, output } from '@angular/core';
import { LoanApi } from '../loan-api';
import { Loan } from '../loan';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-loan-list',
  imports: [NgClass],
  templateUrl: './loan-list.html'
})
export class LoanList {
  private loanApi = inject(LoanApi);

  loans = signal<Loan[]>([]);
  currentPage = signal(0);
  totalPages = signal(1);
  pageSize = signal(20);
  searchTerm = signal('');
  loading = signal(false);

  readonly pageSizeOptions = [10, 20, 50];

  editRequested = output<Loan>();
  deleteRequested = output<Loan>();

  constructor() {
    this.fetchPage(0);
  }

  fetchPage(page: number) {
    this.loading.set(true);
    this.loanApi.getLoans(page, this.pageSize(), this.searchTerm()).subscribe({
      next: data => {
        this.loans.set(data.content);
        this.totalPages.set(data.totalPages);
        this.currentPage.set(data.number);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  refresh() {
    this.fetchPage(this.currentPage());
  }

  nextPage() {
    if (this.currentPage() + 1 < this.totalPages()) {
      this.fetchPage(this.currentPage() + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.fetchPage(this.currentPage() - 1);
    }
  }

  onPageSizeChange(size: string) {
    this.pageSize.set(Number(size));
    this.fetchPage(0);
  }

  onSearchInput(term: string) {
    this.searchTerm.set(term);
    this.fetchPage(0); // reset to page 0 — search result set is different from unfiltered
  }
}
