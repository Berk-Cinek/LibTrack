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
import {BookCatalog} from './books/book-catalog/book-catalog';
import { MyLoans } from './loans/my-loans/my-loans';
import {AdminPanel} from './admin/admin-panel/admin-panel';

export const routes: Routes = [
  { path: '',
    redirectTo: 'bookCatalog',
    pathMatch: 'full'
  },
  {
    path: "bookCatalog",
    component: BookCatalog,
    title: "Book Catalog"
  },
  {
    path: 'myLoans',
    component: MyLoans,
    title: "List all loans of user"
  },
  {
    path: 'loanCreate',
    component: LoanCreate,
    title: "Creation of loan for borrowing a book by id"
  },
  {
    path: 'adminPanel',
    component: AdminPanel,
    title: "Admin panel for CRUD"
  }
];
