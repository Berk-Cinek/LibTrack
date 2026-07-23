import { Component, signal, inject } from '@angular/core';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-delete',
  imports: [],
  templateUrl: './book-delete.html'
})
export class BookDelete {
  private bookApi = inject(BookApi);
  book = signal<Book | null>(null);
  currentId = signal('');

  deleteBook(id: String){
    this.bookApi.bookDelete(Number(id)).subscribe({
      next: response => {
        console.log('status:', response.status);
        if (response.status === 204) {
          alert('Deleted successfully');
        }
      },
      error: err => {
        console.log('error status:', err.status);
        alert(`Failed: ${err.error.message}`);
      },
    });
  }
}
