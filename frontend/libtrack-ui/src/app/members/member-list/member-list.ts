import { Component, inject, OnInit, signal } from '@angular/core';
import { MemberApi } from '../member-api';
import { Member } from '../member';

@Component({
  selector: 'app-member-list',
  imports: [],
  templateUrl: './member-list.html',
  styleUrl: './member-list.css',
})
export class MemberList {
  private memberApi = inject(MemberApi);
  members = signal< Member[] >([]);

  ngOnInit() {
    this.memberApi.getMembers().subscribe(data => {
      console.log(data.content);
      this.members.set(data.content);
    });
  }

}
