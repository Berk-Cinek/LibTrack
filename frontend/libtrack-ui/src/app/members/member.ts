import { Loan } from '../loans/loan';

export interface Member {

  id: number;
  memberNo: number;
  fullName: string;
  email: string;
  isActive: boolean;
  createdAt: string;
  loans: Loan[];
}
