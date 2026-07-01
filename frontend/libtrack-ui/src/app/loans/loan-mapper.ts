import { Loan, CreateLoanRequest } from  './loan'

export function formToLoan(formValue: any): CreateLoanRequest {
  return {
    memberId: Number(formValue.memberId),
    bookId: Number(formValue.bookId),
  };
}

export function formToPartialLoan(formValue: any): Partial<Loan> {
  const loan: Partial<Loan> = {};

  if (formValue.dueDate) loan.dueDate = formValue.dueDate;
  if (formValue.returnedAt) loan.returnedAt = formValue.returnedAt;
  if (formValue.status) loan.status = formValue.status;

  return loan;
}
