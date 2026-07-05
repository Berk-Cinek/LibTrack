import { Component, input, output } from '@angular/core';
import { Book } from '../../book';

@Component({
  selector: 'app-book-detail-modal',
  imports: [],
  templateUrl: './book-detail-modal.html',
  styleUrl: './book-detail-modal.css',
})
export class BookDetailModal {
  book = input.required<Book>();
  closed = output<void>();
}
