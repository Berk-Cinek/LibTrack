import { Component, inject, input, output, effect } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToPartialMember } from '../member-mapper';
import { MemberApi } from '../member-api';
import { Member } from '../member';

@Component({
  selector: 'app-member-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './member-partial-update.html'
})
export class MemberPartialUpdate {
  private memberApi = inject(MemberApi);
  private formBuilder = inject(FormBuilder);

  member = input<Member | null>(null);
  saved = output<Member>();

  memberForm = this.formBuilder.group({
    memberNo: [''],
    fullName: [''],
    email: [''],
    isActive: [false],
  });

  constructor() {
    effect(() => {
      const m = this.member();
      if (m) {
        this.memberForm.patchValue({
          memberNo: String(m.memberNo),
          fullName: m.fullName,
          email: m.email,
          isActive: m.isActive,
        });
      }
    });
  }

  submit() {
    const m = this.member();
    if (!m) return;

    const changes = formToPartialMember(this.memberForm.value);
    this.memberApi.PartialUpdateMember(m.id, changes).subscribe({
      next: updated => this.saved.emit(updated),
      error: (err: HttpErrorResponse) => alert(err.error?.message ?? 'Update failed'),
    });
  }
}
