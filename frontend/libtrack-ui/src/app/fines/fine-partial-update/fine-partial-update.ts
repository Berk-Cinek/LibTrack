import { Component, inject, input, output, effect } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToPartialFine } from '../fine-mapper';
import { FineApi } from '../fine-api';
import { Fine } from '../fine';

@Component({
  selector: 'app-fine-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './fine-partial-update.html'
})
export class FinePartialUpdate {
  private fineApi = inject(FineApi);
  private formBuilder = inject(FormBuilder);

  fine = input<Fine | null>(null);
  saved = output<Fine>();

  fineForm = this.formBuilder.group({
    amount: [''],
    isPaid: [false],
  });

  constructor() {
    effect(() => {
      const f = this.fine();
      if (f) {
        this.fineForm.patchValue({
          amount: String(f.amount),
          isPaid: f.isPaid,
        });
      }
    });
  }

  submit() {
    const f = this.fine();
    if (!f) return;

    const changes = formToPartialFine(this.fineForm.value);
    this.fineApi.partialUpdate(f.id, changes).subscribe({
      next: updated => this.saved.emit(updated),
      error: (err: HttpErrorResponse) => alert(err.error?.message ?? 'Update failed'),
    });
  }
}
