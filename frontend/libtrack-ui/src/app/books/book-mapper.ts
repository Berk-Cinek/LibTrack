import {Book} from './book';

export function formToBook(formValue: any): Partial<Book> {
  const book: Partial<Book> = {};
  book.title  = formValue.title;
  book.author = formValue.author;
  book.genre  = formValue.genre;

  book.isbn            = Number(formValue.isbn);
  book.totalCopies     = Number(formValue.totalCopies);
  book.availableCopies = Number(formValue.availableCopies);

  return book;
  }

export function formToPartialBook(formValue: any): Partial<Book> {
  const book: Partial<Book> = {};
  if (formValue.title)  book.title  = formValue.title;
  if (formValue.author) book.author = formValue.author;
  if (formValue.genre)  book.genre  = formValue.genre;
  if (formValue.isbn !== '' && formValue.isbn !== null)
    book.isbn = Number(formValue.isbn);
  if (formValue.totalCopies !== '' && formValue.totalCopies !== null)
    book.totalCopies = Number(formValue.totalCopies);
  if (formValue.availableCopies !== '' && formValue.availableCopies !== null)
    book.availableCopies = Number(formValue.availableCopies);
  return book;
}
