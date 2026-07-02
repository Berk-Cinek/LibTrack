import { Loan } from '../loans/loan';

export interface Fine {
  id: number;
  loanDto: Loan;
  daysOverdue: number;
  amount: number;
  isPaid: boolean;
  paidAt: string | null;
}
