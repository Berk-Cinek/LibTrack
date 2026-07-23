import { Component, inject, signal } from '@angular/core';
import { FineApi } from '../fine-api';
import { Fine } from '../fine';

@Component({
  selector: 'app-fine-id',
  imports: [],
  templateUrl: './fine-id.html'
})
export class FineId {
  private fineApi = inject(FineApi);
  fine = signal<Fine | null>(null);
  currentId = signal('');

  getFineById(id: string) {
    this.fineApi.getFineById(Number(id)).subscribe(data => {
      this.fine.set(data);
    });
  }
}
