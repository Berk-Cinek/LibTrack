import { Component, input, output } from '@angular/core';
import { Book } from '../../book';

@Component({
  selector: 'app-book-card',
  imports: [],
  templateUrl: './book-card.html'
})
export class BookCard {
  book = input.required<Book>();
  select = output<Book>();

  onSelect() {
    this.select.emit(this.book());
  }
}
