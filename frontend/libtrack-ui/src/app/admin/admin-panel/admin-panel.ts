import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../auth/auth-service';

import { BookCreate } from '../../books/book-create/book-create';
import { BookPartialUpdate } from '../../books/book-partial-update/book-partial-update';
import { BookId } from '../../books/book-id/book-id';
import { BookList } from '../../books/book-list/book-list';
import { BookDelete } from '../../books/book-delete/book-delete';

import { MemberList } from '../../members/member-list/member-list';
import { MemberId } from '../../members/member-id/member-id';
import { MemberCreate } from '../../members/member-create/member-create';
import { MemberDelete } from '../../members/member-delete/member-delete';
import { MemberPartialUpdate } from '../../members/member-partial-update/member-partial-update';

import { LoanCreate} from '../../loans/loan-create/loan-create';
import { LoanDelete } from '../../loans/loan-delete/loan-delete'
import { LoanList } from  '../../loans/loan-list/loan-list';
import { LoanId } from '../../loans/loan-id/loan-id';
import { LoanPartialUpdate } from '../../loans/loan-partial-update/loan-partial-update';

import { FineDelete } from '../../fines/fine-delete/fine-delete';
import { FineList } from '../../fines/fine-list/fine-list';
import { FineId } from '../../fines/fine-id/fine-id';
import { FinePartialUpdate } from '../../fines/fine-partial-update/fine-partial-update';

type AdminTab = 'books' | 'members' | 'loans' | 'fines';

@Component({
  selector: 'app-admin-panel',
  imports: [
    BookCreate,
    BookPartialUpdate,
    BookId,
    BookList,
    BookDelete,
    MemberList,
    MemberId,
    MemberCreate,
    MemberDelete,
    MemberPartialUpdate,
    LoanCreate,
    LoanDelete,
    LoanList,
    LoanId,
    LoanPartialUpdate,
    FineDelete,
    FineList,
    FineId,
    FinePartialUpdate,
  ],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css',
})
export class AdminPanel {
  authService = inject(AuthService);
  activeTab = signal<AdminTab>('books');
}
