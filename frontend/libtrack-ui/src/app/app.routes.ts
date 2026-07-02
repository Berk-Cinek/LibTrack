import { Routes } from '@angular/router';
import {BookList} from './books/book-list/book-list';
import {BookId} from './books/book-id/book-id'
import {BookPartialUpdate} from './books/book-partial-update/book-partial-update'
import {BookFullUpdate} from './books/book-full-update/book-full-update';
import {BookDelete} from './books/book-delete/book-delete';
import {MemberList} from './members/member-list/member-list';
import {MemberId} from './members/member-id/member-id';
import {BookCreate} from './books/book-create/book-create';
import {MemberCreate} from './members/member-create/member-create';
import {MemberFullUpdate} from './members/member-full-update/member-full-update';
import {MemberPartialUpdate} from './members/member-partial-update/member-partial-update';
import {MemberDelete} from './members/member-delete/member-delete';
import {LoanList} from './loans/loan-list/loan-list';
import {LoanId} from './loans/loan-id/loan-id';
import {LoanCreate} from './loans/loan-create/loan-create';
import {LoanPartialUpdate} from './loans/loan-partial-update/loan-partial-update';
import {LoanDelete} from './loans/loan-delete/loan-delete';
import {FineId} from './fines/fine-id/fine-id';
import {FinePartialUpdate} from './fines/fine-partial-update/fine-partial-update';
import {FineDelete} from './fines/fine-delete/fine-delete';
import {FineList} from './fines/fine-list/fine-list';

export const routes: Routes = [
  { path: '',
    redirectTo: 'bookList',
    pathMatch: 'full' },
  {
    path: "bookList",
    component: BookList,
    title: "Listing Of all books"
  },
  {
    path: "createBook",
    component: BookCreate,
    title: "Create one book"
  },
  {
    path: "booksById",
    component: BookId,
    title: "Select a specific book"
  },
  {
    path: "bookPartialUpdateById",
    component: BookPartialUpdate,
    title: "Partial update based on matching Id"
  },
  {
    path: "bookFullUpdateById",
    component: BookFullUpdate,
    title: "Full Book update based on matching Id"
  },
  {
    path: "bookDelete",
    component: BookDelete,
    title: "Delete a book by id"
  },
  {
    path: "memberList",
    component: MemberList,
    title: "List of all members"
  },
  {
    path: "memberById",
    component: MemberId,
    title: "Member display by Id"
  },
  {
    path: "memberCreate",
    component: MemberCreate,
    title: "Creating one member"
  },
  {
    path: "memberFullUpdate",
    component: MemberFullUpdate,
    title: "Fully update all fields of a given Member"
  },
  {
    path: "memberPartialUpdate",
    component: MemberPartialUpdate,
    title: "Partially update chosen fields of a given Member"
  },
  {
    path: "memberDelete",
    component: MemberDelete,
    title: "Delete member via Id"
  },
  {
    path: "loanList",
    component: LoanList,
    title: "List of loans"
  },
  {
    path: "loanId",
    component: LoanId,
    title: "Loan displayed via ID"
  },
  {
    path: "loanCreate",
    component: LoanCreate,
    title: "Create a Loan"
  },
  {
    path: "loanPartialUpdate",
    component: LoanPartialUpdate,
    title: "Partial update of loan via id"
  },
  {
    path: "loanDelete",
    component: LoanDelete,
    title: "Delete loan via ID"
  },
  {
    path: "fineList",
    component: FineList,
    title: "List all fines"
  },
  {
    path: "fineById",
    component: FineId,
    title: "Find a fine by Id"
  },
  {
    path: "finePartialUpdate",
    component: FinePartialUpdate,
    title: "Partial Update For Fines"
  },
  {
    path: "fineDelete",
    component: FineDelete,
    title: "Delete Fine by Id"
  }
];
