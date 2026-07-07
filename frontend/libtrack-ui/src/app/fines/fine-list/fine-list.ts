import { Component, inject, OnInit, signal, output } from '@angular/core';
import { FineApi } from '../fine-api';
import { Fine } from '../fine';

@Component({
  selector: 'app-fine-list',
  imports: [],
  templateUrl: './fine-list.html',
  styleUrl: './fine-list.css',
})
export class FineList implements OnInit {
  private fineApi = inject(FineApi);

  fines = signal<Fine[]>([]);
  page = signal(0);
  size = signal(20);
  search = signal('');
  totalPages = signal(0);
  first = signal(true);
  last = signal(true);

  editRequested = output<Fine>();
  deleteRequested = output<Fine>();

  ngOnInit() {
    this.loadFines();
  }

  loadFines() {
    this.fineApi.getFinesAdmin(this.page(), this.size(), this.search()).subscribe(data => {
      this.fines.set(data.content);
      this.totalPages.set(data.totalPages);
      this.first.set(data.first);
      this.last.set(data.last);
    });
  }

  nextPage() {
    if (this.page() + 1 < this.totalPages()) {
      this.page.set(this.page() + 1);
      this.loadFines();
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.page.set(this.page() - 1);
      this.loadFines();
    }
  }

  onSearchInput(value: string) {
    this.search.set(value);
    this.page.set(0);
    this.loadFines();
  }

  onSizeChange(value: string) {
    this.size.set(Number(value));
    this.page.set(0);
    this.loadFines();
  }
}
