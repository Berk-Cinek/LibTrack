import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from  '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { formToMember } from '../member-mapper';
import { MemberApi } from '../member-api';
import { Member } from  '../member';

@Component({
  selector: 'app-member-create',
  imports: [ReactiveFormsModule],
  templateUrl: './member-create.html',
  styleUrl: './member-create.css',
})
export class MemberCreate {
  private memberApi = inject(MemberApi);
  private formBuilder = inject(FormBuilder);
  member = signal<Member | null>(null);

  created = output<Member>();

  memberForm = this.formBuilder.group({
    memberNo: ['', Validators.required],
    fullName: ['', Validators.required],
    email: ['', Validators.required],
    isActive: [false],
  })

  onCreateMember() {
    const request = formToMember(this.memberForm.value);
    this.memberApi.createMember(request).subscribe({
      next: member => {
        this.created.emit(member);
        this.memberForm.reset();
      },
      error: (err: HttpErrorResponse) => {
        alert(err.error?.message ?? 'Create failed');
      },
    });
  }
}
