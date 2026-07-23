import { Component, inject, signal } from '@angular/core';
import { MemberApi } from '../member-api';
import { Member } from '../member';

@Component({
  selector: 'app-member-id',
  imports: [],
  templateUrl: './member-id.html'
})
export class MemberId {
  private memberApi = inject(MemberApi);
  member = signal<Member | null>(null);
  currentId = signal('');

  getmemberById(id: String){
    return this.memberApi.getMemberById(Number(id)).subscribe(data => {
      console.log('single member data:', data);
      this.member.set(data)
    })
  }
}
