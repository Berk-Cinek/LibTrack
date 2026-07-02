import { Fine } from './fine';

export function formToPartialFine(formValue: any): Partial<Fine> {
  const fine: Partial<Fine> = {};
  if (formValue.isPaid !== null && formValue.isPaid !== undefined) fine.isPaid = formValue.isPaid;
  if (formValue.amount !== '' && formValue.amount !== null) fine.amount = Number(formValue.amount);
  return fine;
}
