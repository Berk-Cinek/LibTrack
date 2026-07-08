import { Routes } from '@angular/router';
import { LoanCreate } from './loans/loan-create/loan-create';
import { BookCatalog } from './books/book-catalog/book-catalog';
import { MyLoans } from './loans/my-loans/my-loans';
import { AdminPanel } from './admin/admin-panel/admin-panel';
import { adminGuard } from './auth/route-guards/admin-guard';
import { authGuard } from  './auth/route-guards/auth-guard';

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
    canActivate: [authGuard],
    title: "List all loans of user"
  },
  {
    path: 'loanCreate',
    component: LoanCreate,
    canActivate: [authGuard],
    title: "Creation of loan for borrowing a book by id"
  },
  {
    path: 'adminPanel',
    component: AdminPanel,
    canActivate: [adminGuard],
    title: "Admin panel for CRUD"
  }
];
