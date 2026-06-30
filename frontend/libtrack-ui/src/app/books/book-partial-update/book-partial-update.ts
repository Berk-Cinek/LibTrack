import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule} from  '@angular/forms';
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
  book = signal<Book | null>(null);
  result = signal<Book | null>(null);
  currentId = signal('');

  bookForm = this.formBuilder.group({
    isbn: [''],
    title: [''],
    author: [''],
    genre: [''],
    totalCopies: [''],
    availableCopies: [''],
  })

  submit(id: string){
    const all = this.bookForm.value;
    const changes: Partial<Book> = {};
    for (const key in all) {
      const value = (all as any)[key];
      if (value !== '' && value !== null) {
        (changes as any)[key] = value;
      }
    }
    this.partialUpdate(id, changes);
  }

  partialUpdate(id : string, changes: Partial<Book>){
    this.bookApi.bookPartialUpdate(Number(id), changes).subscribe({
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
