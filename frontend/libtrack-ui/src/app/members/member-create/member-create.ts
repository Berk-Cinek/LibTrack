import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from  '@angular/forms';
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

  memberForm = this.formBuilder.group({
    memberNo: ['', Validators.required],
    fullName: ['', Validators.required],
    email: ['', Validators.required],
    isActive: [false],
  })

  createOneMember(){
    const created = formToMember(this.memberForm.value);
    this.memberApi.createMember(created).subscribe({
      next: createdMember => {
        console.log(createdMember);
        alert("Member Created")
        this.member.set(createdMember)
      },
      error: () =>{
        alert("Creation Failed - Please try again")
      },
    })
  }
}
