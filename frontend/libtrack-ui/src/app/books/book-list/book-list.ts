import { Component, inject, OnInit, signal} from '@angular/core';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-list',
  imports: [],
  templateUrl: './book-list.html',
  styleUrl: './book-list.css',
})
export class BookList implements OnInit{
  private bookApi = inject(BookApi);
  books = signal<Book[]>([]);

  ngOnInit() {
    this.bookApi.getBooks().subscribe(data => {
      this.books.set (data.content);
  });
  }
}
