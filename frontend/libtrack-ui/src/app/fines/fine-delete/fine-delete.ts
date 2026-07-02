import { Component, inject, signal } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { FineApi } from '../fine-api';

@Component({
  selector: 'app-fine-delete',
  imports: [],
  templateUrl: './fine-delete.html',
  styleUrl: './fine-delete.css',
})
export class FineDelete {
  private fineApi = inject(FineApi);
  currentId = signal('');

  deleteFine(id: string) {
    this.fineApi.deleteFine(Number(id)).subscribe({
      next: (response: HttpResponse<void>) => {
        alert('Fine deleted successfully');
      },
      error: (err: HttpErrorResponse) => {
        alert(err.error.message);
      },
    });
  }
}
