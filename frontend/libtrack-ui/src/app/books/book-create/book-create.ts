import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from  '@angular/forms';
import { formToBook } from '../book-mapper';
import { BookApi } from '../book-api';
import { Book } from '../book';


@Component({
  selector: 'app-book-create',
  imports: [ReactiveFormsModule],
  templateUrl: './book-create.html',
  styleUrl: './book-create.css',
})
export class BookCreate {
  private bookApi = inject(BookApi);
  private formBuilder = inject(FormBuilder);
  book = signal<Book | null>(null);

  bookForm = this.formBuilder.group({
    isbn: ['', Validators.required],
    title: ['', Validators.required],
    author: ['', Validators.required],
    genre: ['', Validators.required],
    totalCopies: ['', Validators.required],
    availableCopies: ['', Validators.required],
  })

  createOneBook(){
    const created = formToBook(this.bookForm.value);
    this.bookApi.createBook(created).subscribe({
      next: createdBook => {
        alert("Book created!")
        this.book.set(createdBook);
      },
      error: () => {
        alert("Creation Failed - Please try again")
      },
    })
  }

}
