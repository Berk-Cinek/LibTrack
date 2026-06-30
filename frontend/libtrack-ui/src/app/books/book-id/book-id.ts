import { Component, inject, signal} from '@angular/core';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-id',
  imports: [],
  templateUrl: './book-id.html',
  styleUrl: './book-id.css',
})
export class BookId{
  private bookApi = inject(BookApi);
  book = signal<Book | null>(null);
  currentId = signal('');

  loadOne(id : string){
    this.bookApi.getBookById(Number(id)).subscribe(data => this.book.set(data));
  }
}
