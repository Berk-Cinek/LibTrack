import { Component, inject, input, output, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../auth/auth-service';
import { LoanApi } from '../../../loans/loan-api';
import { Loan } from '../../../loans/loan';
import { Book } from '../../book';

@Component({
  selector: 'app-book-detail-modal',
  imports: [],
  templateUrl: './book-detail-modal.html',
  styleUrl: './book-detail-modal.css',
})
export class BookDetailModal {
  authService = inject(AuthService);
  private loanApi = inject(LoanApi);

  book = input.required<Book>();
  closed = output<void>();
  borrowed = output<Loan>();
  borrowError = signal('');

  borrow(book: Book) {
    this.borrowError.set('');
    this.loanApi.borrowBook(book.id).subscribe({
      next: loan => {
        this.borrowed.emit(loan);
        this.closed.emit();
      },
      error: (err: HttpErrorResponse) => {
        this.borrowError.set(err.error?.message ?? 'Could not borrow this book.');
      },
    });
  }
}
