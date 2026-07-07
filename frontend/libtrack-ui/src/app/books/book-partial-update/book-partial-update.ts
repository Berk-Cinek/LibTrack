import { Component, inject, input, output, effect } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToPartialBook } from '../book-mapper';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './book-partial-update.html',
  styleUrl: './book-partial-update.css',
})
export class BookPartialUpdate {
  private bookApi = inject(BookApi);
  private formBuilder = inject(FormBuilder);

  book = input<Book | null>(null);
  saved = output<Book>();

  bookForm = this.formBuilder.group({
    isbn: [''],
    title: [''],
    author: [''],
    genre: [''],
    totalCopies: [''],
    availableCopies: [''],
  });

  constructor() {
    effect(() => {
      const thisBook = this.book();
      if (thisBook) {
        this.bookForm.patchValue({
          isbn: String(thisBook.isbn),
          title: thisBook.title,
          author: thisBook.author,
          genre: thisBook.genre,
          totalCopies: String(thisBook.totalCopies),
          availableCopies: String(thisBook.availableCopies),
        });
      }
    });
  }

  submit() {
    const thisBook = this.book();
    if (!thisBook) return;

    const changes = formToPartialBook(this.bookForm.value);
    this.bookApi.bookPartialUpdate(thisBook.id, changes).subscribe({
      next: updated => this.saved.emit(updated),
      error: (err: HttpErrorResponse) => alert(err.error?.message ?? 'Update failed'),
    });
  }
}
