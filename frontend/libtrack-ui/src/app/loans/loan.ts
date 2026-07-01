import {Book} from '../books/book'

export type LoanStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE';

export interface Loan {

  id: number;
  memberId: number;
  bookDto: Book;
  borrowedAt: string;
  dueDate: string;
  returnedAt: string | null;
  status: LoanStatus;
}

export interface CreateLoanRequest {
  memberId: number;
  bookId: number;
}
