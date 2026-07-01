import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from  '@angular/forms';
import { formToMember } from '../member-mapper';
import { MemberApi } from '../member-api';
import { Member } from  '../member';

@Component({
  selector: 'app-member-full-update',
  imports: [ReactiveFormsModule],
  templateUrl: './member-full-update.html',
  styleUrl: './member-full-update.css',
})
export class MemberFullUpdate {
  private memberApi = inject(MemberApi);
  private formBuilder = inject(FormBuilder);
  member = signal< Member | null >(null);
  currentId = signal('');

  memberForm = this.formBuilder.group({
    memberNo: ['', Validators.required],
    fullName: ['', Validators.required],
    email: ['', Validators.required],
    isActive: [false],
  })

  fullUpdateMember(id: String){
    const changes = formToMember(this.memberForm.value);
    this.memberApi.fullUpdateMember(Number(id), changes).subscribe({
      next: updatedMember => {
        alert("Updated successfully");
        this.member.set(updatedMember);
      },
      error: () =>{
        alert("Update failed- please try again")
      },
    })
  }
}
