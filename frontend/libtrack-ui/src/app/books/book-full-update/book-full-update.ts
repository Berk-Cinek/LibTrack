import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule} from  '@angular/forms';
import { Validators } from '@angular/forms';
import { formToBook } from '../book-mapper'
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
    if (this.bookForm.invalid){
      return alert("please fill all the provided fields")
    }
    const changes = formToBook(this.bookForm.value);
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
}
