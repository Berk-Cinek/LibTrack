import {Book} from '../books/book'

export type LoanStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE';

export interface Loan {

  id: number;
  memberId: number;
  Book: Book;
  borrowedAt: string;
  dueDate: string;
  returnedAt: string;
  status: LoanStatus;
}
