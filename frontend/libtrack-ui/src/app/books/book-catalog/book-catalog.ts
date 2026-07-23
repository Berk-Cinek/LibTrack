import { Component, inject, signal, OnInit } from '@angular/core';
import { BookApi } from '../book-api';
import { Book } from '../book';
import { SearchList } from '../../search-list/search-list';
import { BookCard } from './book-card/book-card';
import { BookDetailModal } from './book-detail-modal/book-detail-modal';

@Component({
  selector: 'app-book-catalog',
  imports: [SearchList, BookCard, BookDetailModal],
  templateUrl: './book-catalog.html'
})
export class BookCatalog implements OnInit {
  private bookApi = inject(BookApi);

  books = signal<Book[]>([]);
  loading = signal(false);
  selectedBook = signal<Book | null>(null);

  bookFilter = (book: Book, term: string) =>
    book.title.toLowerCase().includes(term) ||
    book.author.toLowerCase().includes(term) ||
    book.genre.toLowerCase().includes(term);

  ngOnInit() {
    this.loading.set(true);
    this.bookApi.getBooks().subscribe({
      next: data => {
        this.books.set(data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
