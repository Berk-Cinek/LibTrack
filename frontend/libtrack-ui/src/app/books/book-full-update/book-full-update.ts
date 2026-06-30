import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule} from  '@angular/forms';
import { Validators } from '@angular/forms';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-full-update',
  imports: [ReactiveFormsModule],
  templateUrl: './book-full-update.html',
  styleUrl: './book-full-update.css',
})
export class BookFullUpdate {
  private bookApi = inject(BookApi);
  private formBuilder = inject(FormBuilder);
  book = signal<Book | null>(null);
  result = signal<Book | null>(null);
  currentId = signal('');

  bookForm = this.formBuilder.group({
    isbn: ['', Validators.required],
    title: ['', Validators.required],
    author: ['', Validators.required],
    genre: ['', Validators.required],
    totalCopies: ['', Validators.required],
    availableCopies: ['', Validators.required],
  })

  fullUpdateBook(id: String){

    console.log('id received:', id);
    if (this.bookForm.invalid){
      return alert("please fill all the provided fields")
    }
    const changes = this.toBook(this.bookForm.value);
    this.bookApi.bookFullUpdate(Number(id), changes).subscribe({
      next: updatedBook => {
        alert('Updated successfully');
        this.result.set(updatedBook);
      },
      error: () => {
        alert('Update failed - please try again');
      },
    })
  }

  private toBook(formValue: any): Partial<Book> {
    const book: Partial<Book> = {};
    if (formValue.title)  book.title  = formValue.title;
    if (formValue.author) book.author = formValue.author;
    if (formValue.genre)  book.genre  = formValue.genre;

    if (formValue.isbn)            book.isbn            = Number(formValue.isbn);
    if (formValue.totalCopies)     book.totalCopies     = Number(formValue.totalCopies);
    if (formValue.availableCopies) book.availableCopies = Number(formValue.availableCopies);

    return book;
  }
}
