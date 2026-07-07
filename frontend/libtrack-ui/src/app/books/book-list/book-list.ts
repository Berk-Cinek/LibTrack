import { Component, inject, OnInit, signal, output} from '@angular/core';
import { BookApi } from '../book-api';
import { Book } from '../book';

@Component({
  selector: 'app-book-list',
  imports: [],
  templateUrl: './book-list.html',
  styleUrl: './book-list.css',
})
export class BookList implements OnInit {
  private bookApi = inject(BookApi);

  books = signal<Book[]>([]);
  page = signal(0);
  size = signal(20);
  search = signal('');
  totalPages = signal(0);
  first = signal(true);
  last = signal(true);

  editRequested = output<Book>();
  deleteRequested = output<Book>();

  ngOnInit() {
    this.loadBook();
  }

  loadBook() {
    this.bookApi.getBooksAdmin(this.page(), this.size(), this.search()).subscribe(data => {
      this.books.set(data.content);
      this.totalPages.set(data.totalPages);
      this.first.set(data.first);
      this.last.set(data.last);
    });
  }

  nextPage() {
    if (this.page() + 1 < this.totalPages()) {
      this.page.set(this.page() + 1);
      this.loadBook();
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.page.set(this.page() - 1);
      this.loadBook();
    }
  }

  onSearchInput(value: string){
    this.search.set(value);
    this.page.set(0);
    this.loadBook()
  }

  onSizeChange(value: string){
    this.size.set(Number(value));
    this.page.set(0);
    this.loadBook();
  }
}
