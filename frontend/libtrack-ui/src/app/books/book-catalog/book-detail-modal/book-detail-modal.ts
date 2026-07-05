import { Component, input, output } from '@angular/core';
import { Book } from '../../book';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-book-detail-modal',
  imports: [RouterLink],
  templateUrl: './book-detail-modal.html',
  styleUrl: './book-detail-modal.css',
})
export class BookDetailModal {
  book = input.required<Book>();
  closed = output<void>();
}
