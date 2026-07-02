import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToPartialFine } from '../fine-mapper';
import { FineApi } from '../fine-api';
import { Fine } from '../fine';

@Component({
  selector: 'app-fine-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './fine-partial-update.html',
  styleUrl: './fine-partial-update.css',
})
export class FinePartialUpdate {
  private fineApi = inject(FineApi);
  private formBuilder = inject(FormBuilder);
  result = signal<Fine | null>(null);
  currentId = signal('');

  fineForm = this.formBuilder.group({
    amount: [''],
    isPaid: [false],
  });

  partialUpdateFine(id: string) {
    const changes = formToPartialFine(this.fineForm.value);
    this.fineApi.partialUpdate(Number(id), changes).subscribe({
      next: fine => {
        this.result.set(fine);
      },
      error: (err: HttpErrorResponse) => {
        alert(err.error.message);
      },
    });
  }
}
