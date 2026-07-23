import { Component, input, output, contentChild, TemplateRef, signal, computed } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';

@Component({
  selector: 'app-search-list',
  imports: [NgTemplateOutlet],
  templateUrl: './search-list.html'
})
export class SearchList<T> {
  items = input.required<T[]>();
  loading = input<boolean>(false);
  searchPlaceholder = input<string>('Search…');
  filterFn = input.required<(item: T, term: string) => boolean>();
  emptyMessage = input<string>('No results match your search.');

  select = output<T>();

  itemTemplate = contentChild.required(TemplateRef);

  searchTerm = signal('');

  filteredItems = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const all = this.items();
    if (!term) return all;
    return all.filter(item => this.filterFn()(item, term));
  });

  onSearchInput(value: string) {
    this.searchTerm.set(value);
  }
}
