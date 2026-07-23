import { Component, inject, signal } from '@angular/core';
import { MemberApi } from '../member-api';
import { Member } from '../member';

@Component({
  selector: 'app-member-delete',
  imports: [],
  templateUrl: './member-delete.html'
})
export class MemberDelete {
  private memberApi = inject(MemberApi);
  member = signal<Member | null>(null);
  currentId = signal('');

  deleteMember(id: string){
    this.memberApi.memberDelete(Number(id)).subscribe({
      next: response => {
        console.log("Status", response.status);
        if (response.status === 204) {
          alert('Deleted successfully');
        }
      },
      error: err => {
        console.log('error status:', err.status);
        alert(`Failed: ${err.error.message}`);
      },
    })
  }
}
