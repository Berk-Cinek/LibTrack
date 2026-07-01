import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule} from  '@angular/forms';
import { formToPartialMember } from '../member-mapper';
import { MemberApi } from '../member-api';
import { Member } from  '../member';

@Component({
  selector: 'app-member-partial-update',
  imports: [ReactiveFormsModule],
  templateUrl: './member-partial-update.html',
  styleUrl: './member-partial-update.css',
})
export class MemberPartialUpdate {
  private memberApi = inject(MemberApi);
  private formBuilder = inject(FormBuilder);
  member = signal< Member | null >(null);
  currentId = signal('');

  memberForm = this.formBuilder.group({
    memberNo: [''],
    fullName: [''],
    email: [''],
    isActive: [false],
  })

  partialUpdateMember(id: String){
    const changes = formToPartialMember(this.memberForm.value);
    this.memberApi.PartialUpdateMember(Number(id), changes).subscribe({
      next: updatedMember => {
        console.log('sending changes:', changes);
        alert("Member updated successfully");
        this.member.set(updatedMember);
      },
      error: () => {
       alert("Update Failed - Please Try again");
      },
    })
  }
}
