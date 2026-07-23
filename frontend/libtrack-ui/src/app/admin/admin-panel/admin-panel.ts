import { Component, inject, signal, viewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../auth/auth-service';

import { BookCreate } from '../../books/book-create/book-create';
import { BookPartialUpdate } from '../../books/book-partial-update/book-partial-update';
import { BookList } from '../../books/book-list/book-list';
import { Book } from '../../books/book';
import { BookApi } from '../../books/book-api';

import { MemberList } from '../../members/member-list/member-list';
import { MemberCreate } from '../../members/member-create/member-create';
import { MemberPartialUpdate } from '../../members/member-partial-update/member-partial-update';
import { Member } from '../../members/member';
import { MemberApi } from '../../members/member-api';

import { LoanCreate } from '../../loans/loan-create/loan-create';
import { LoanList } from '../../loans/loan-list/loan-list';
import { LoanPartialUpdate } from '../../loans/loan-partial-update/loan-partial-update';
import { Loan } from '../../loans/loan';
import { LoanApi } from '../../loans/loan-api';

import { FineList } from '../../fines/fine-list/fine-list';
import { FinePartialUpdate } from '../../fines/fine-partial-update/fine-partial-update';
import { Fine } from '../../fines/fine';
import { FineApi } from '../../fines/fine-api';
import { NgClass } from '@angular/common';

type AdminTab = 'books' | 'members' | 'loans' | 'fines';

@Component({
  selector: 'app-admin-panel',
  imports: [
    BookCreate, BookPartialUpdate, BookList,
    MemberCreate, MemberPartialUpdate, MemberList,
    LoanCreate, LoanPartialUpdate, LoanList,
    FinePartialUpdate, FineList, NgClass
  ],
  templateUrl: './admin-panel.html'
})
export class AdminPanel {
  authService = inject(AuthService);
  private bookApi = inject(BookApi);
  private memberApi = inject(MemberApi);
  private loanApi = inject(LoanApi);
  private fineApi = inject(FineApi);

  activeTab = signal<AdminTab>('books');

  selectedBook = signal<Book | null>(null);
  bookList = viewChild(BookList);

  onEditBook(book: Book) { this.selectedBook.set(book); }
  onBookCreated() { this.bookList()?.loadBook(); }
  onBookSaved() { this.selectedBook.set(null); this.bookList()?.loadBook(); }
  onCancelBook() { this.selectedBook.set(null); }
  onDeleteBook(book: Book) {
    if (!confirm(`Delete "${book.title}"? This cannot be undone.`)) return;
    this.bookApi.bookDelete(book.id).subscribe({
      next: () => this.bookList()?.loadBook(),
      error: err => alert(err.error?.message ?? 'Delete failed'),
    });
  }

  selectedMember = signal<Member | null>(null);
  memberList = viewChild(MemberList);

  onMemberCreated() {
    this.memberList()?.loadMembers();   // is this handler here?
  }
  onEditMember(member: Member) { this.selectedMember.set(member); }
  onMemberSaved() { this.selectedMember.set(null); this.memberList()?.loadMembers(); }
  onCancelMember() { this.selectedMember.set(null); }
  onPromoteMember(member: Member) {
    if (!confirm(`Make "${member.fullName}" an admin? They will have full management access.`)) return;
    this.authService.promoteToAdmin(member.id).subscribe({
      next: () => alert(`${member.fullName} is now an admin.`),
      error: (err: HttpErrorResponse) => alert(err.error?.message ?? 'Promotion failed'),
    });
  }
  onDeleteMember(member: Member) {
    if (!confirm(`Delete member "${member.fullName}"? This cannot be undone.`)) return;
    this.memberApi.memberDelete(member.id).subscribe({
      next: () => this.memberList()?.loadMembers(),
      error: err => alert(err.error?.message ?? 'Delete failed'),
    });
  }

  selectedLoan = signal<Loan | null>(null);
  loanList = viewChild(LoanList);

  onEditLoan(loan: Loan) { this.selectedLoan.set(loan); }
  onLoanSaved() {
    this.selectedLoan.set(null);
    this.loanList()?.refresh();
  }
  onCancelLoan() { this.selectedLoan.set(null); }
  onDeleteLoan(loan: Loan) {
    if (!confirm(`Delete this loan of "${loan.bookDto.title}"? This cannot be undone.`)) return;
    this.loanApi.deleteLoan(loan.id).subscribe({
      next: () => this.loanList()?.refresh(),
      error: err => alert(err.error?.message ?? 'Delete failed'),
    });
  }

  selectedFine = signal<Fine | null>(null);
  fineList = viewChild(FineList);

  onEditFine(fine: Fine) { this.selectedFine.set(fine); }
  onFineSaved() { this.selectedFine.set(null); this.fineList()?.loadFines(); }
  onCancelFine() { this.selectedFine.set(null); }
  onDeleteFine(fine: Fine) {
    if (!confirm('Delete this fine? This cannot be undone.')) return;
    this.fineApi.deleteFine(fine.id).subscribe({
      next: () => this.fineList()?.loadFines(),
      error: err => alert(err.error?.message ?? 'Delete failed'),
    });
  }
}
