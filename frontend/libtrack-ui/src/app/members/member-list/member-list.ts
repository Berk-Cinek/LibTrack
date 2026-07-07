import { Component, inject, OnInit, signal } from '@angular/core';
import { MemberApi } from '../member-api';
import { Member } from '../member';

@Component({
  selector: 'app-member-list',
  imports: [],
  templateUrl: './member-list.html',
  styleUrl: './member-list.css',
})

export class MemberList implements OnInit {
  private memberApi = inject(MemberApi);

  members = signal<Member[]>([]);
  page = signal(0);
  size = signal(20);
  search = signal('');
  totalPages = signal(0);
  first = signal(true);
  last = signal(true);

  ngOnInit() {
    this.loadMembers();
  }

  loadMembers() {
    this.memberApi.getMembers(this.page(), this.size(), this.search()).subscribe(data => {
      this.members.set(data.content);
      this.totalPages.set(data.totalPages);
      this.first.set(data.first);
      this.last.set(data.last);
    });
  }

  nextPage() {
    this.page.set(this.page() + 1);
    this.loadMembers();
  }

  prevPage() {
    this.page.set(this.page() - 1);
    this.loadMembers();
  }

  onSearchInput(value: string) {
    this.search.set(value);
    this.page.set(0);
    this.loadMembers();
  }

  onSizeChange(value: string) {
    this.size.set(Number(value));
    this.page.set(0);
    this.loadMembers();
  }
}
